package nschank.engn.play.phys;

import com.google.common.base.Optional;
import nschank.collect.dim.Dimensional;
import nschank.collect.dim.Vector;
import nschank.collect.dim.VectorDimensionalList;
import nschank.collect.tuple.Pair;
import nschank.engn.play.AbstractEntity;
import nschank.engn.play.entity.PropertyMisformatException;
import nschank.engn.play.io.Connection;
import nschank.engn.play.io.Inputs;
import nschank.engn.play.io.eval.Constant;
import nschank.engn.play.io.eval.Evaluator;
import nschank.engn.play.univ.Universe;
import nschank.engn.shape.Drawable;
import nschank.engn.shape.collide.Collidable;
import nschank.engn.shape.collide.Ray;
import nschank.engn.sprite.AnimatedSprite;
import nschank.util.DerivativeList;
import nschank.util.DoubleDerivativeList;
import nschank.util.Interval;
import nschank.util.NMaps;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Map;


/**
 * Created by Nicolas Schank for package nschank.engn.play.phys
 * Created on 1 Oct 2013
 * Last updated on 2 Jun 2014
 *
 * An Entity which is affected by Physics. It has a concept of position, velocity, and higher derivatives, and they are
 * applied in Eulerian symplectic order. Has a concept of mass, force, restitution, and impulse. Draws itself using the
 * shapes assigned to determine its edges.
 *
 * Is able to intake an AI which can instruct it as necessary.
 *
 * @author nschank, Brown University
 * @version 5.1
 */
public abstract class AbstractPhysicsEntity extends AbstractEntity implements PhysicsEntity
{
	public static final String COEFFICIENT_DYNAMIC_MISFORMAT_EXCEPTION
			= "Coefficient of dynamic friction is a required property for a PhysicsEntity, which must have a type of double.";
	public static final String COEFFICIENT_RESTITUTION_MISFORMAT_EXCEPTION
			= "Coefficient of restitution is a required property for a PhysicsEntity, which must have a type of double.";
	public static final String COEFFICIENT_STATIC_MISFORMAT_EXCEPTION
			= "Coefficient of static friction is a required property for a PhysicsEntity, which must have a type of double.";
	public static final String MASS_MISFORMAT_EXCEPTION
			= "Mass is a required property for a PhysicsEntity, which must have a type of double.";

	public static final String MOMENT_OF_INERTIA_MISFORMAT_EXCEPTION = "Moment of inertia cannot be set.";

	public static final double ONE_SECOND = 1_000_000_000d;

	public static final String SHAPE_MISFORMAT_EXCEPTION
			= "Shape is a required property for a PhysicsEntity, which must have a type of Collidable.";


	private double coefficientOfDynamicFrictionSqrt;
	private double coefficientOfRestitutionSqrt;
	private double coefficientOfStaticFrictionSqrt;
	private Vector forces;
	private Vector impulses;
	private double mass;
	private DerivativeList<Vector> pdl;
	private DerivativeList<Double> rdl;
	private double rotationalImpulse;
	private Collidable shape;
	private double torque;

	/**
	 * Creates a PhysicsEntity whose boundaries are assigned by the given Collidable, and
	 * which draws the same Collidable as itself.
	 *
	 * @param universe
	 * 		The {@code Universe} in which this shape exists.
	 * @param properties
	 * 		A descrpition of the shape and visual aspect of this PhysicsEntity
	 */
	public AbstractPhysicsEntity(Universe universe, Map<String, Object> properties)
	{
		super(universe, properties);

		this.forces = Vector.ZERO_2D;
		this.impulses = Vector.ZERO_2D;
		this.rotationalImpulse = 0.0;
		this.torque = 0.0;
		this.pdl = new VectorDimensionalList(new Vector(this.getShape().getCenterPosition()));
		this.rdl = new DoubleDerivativeList(0.0);
		this.initInputs();
		this.initDefaultProperties();
		this.connect("onRemove", new Connection(this, "doPhysicalRemove"));
	}

	/**
	 * @param force
	 * 		A force, represented by a {@code Vector}
	 * @param position
	 * 		The location at which the force must be applied, as a {@code Dimensional}. Generally, should be inside this
	 */
	@Override
	public void applyForceAt(Vector force, Dimensional position)
	{
		this.forces = this.forces.plus(force);
		this.torque += new Vector(position).minus(this.getShape().getCenterPosition()).crossProduct(force)
										   .getCoordinate(2);
	}

	/**
	 * @param impulse
	 * 		An impulse, represented by a {@code Vector}
	 * @param position
	 * 		The location at which this impulse must be applied, as a {@code Dimensional}. Generally, should be inside
	 */
	@Override
	public void applyImpulseAt(Vector impulse, Dimensional position)
	{
		this.impulses = this.impulses.plus(impulse);
		this.rotationalImpulse += new Vector(position).minus(this.getShape().getCenterPosition()).crossProduct(impulse)
													  .getCoordinate(2);
	}

	/**
	 * Apples the given location change to this {@code PhysicsEntity}.
	 *
	 * @param change
	 * 		A {@code Vector} of movement to apply tho this {@code PhysicsEntity}.
	 */
	@Override
	public void applyLocationChange(Vector change)
	{
		this.setCenterPosition(change.plus(this.getShape().getCenterPosition()));
	}

	/**
	 * Stops all motion of any kind. This includes any derivative of position over time.
	 */
	@Override
	public void arrestMotion()
	{
		this.pdl = new VectorDimensionalList(new Vector(this.getCenterPosition()));
		this.rdl = new DoubleDerivativeList(this.getAngle());
	}

	/**
	 * Used for raycasting with this {@code PhysicsEntity}'s shape
	 *
	 * @param ray
	 * 		A {@code Ray} which may be colliding with this {@code PhysicsEntity}
	 *
	 * @return Either the distance from the ray's point to its collision with this {@code PhysicsEntity}, or
	 * {@code Optional.absent()}
	 *
	 * @see nschank.engn.shape.collide.Ray
	 */
	@Override
	public Optional<Double> collisionWith(Ray ray)
	{
		return this.getShape().distanceAlong(ray);
	}

	/**
	 * Whether or not this {@code PhysicsEntity} is colliding with another, given {@code PhysicsEntity}
	 *
	 * @param physicsEntity
	 * 		Another {@code PhysicsEntity} which may be colliding with this one
	 *
	 * @return The {@code PhysCollision} between this {@code PhysicsEntity} and another, if they are colliding. Otherwise,
	 * returns {@code Optional.absent()}
	 */
	@Override
	public Optional<PhysCollision> collisionWith(PhysicsEntity physicsEntity)
	{
		Optional<Collidable.Collision> shapeCollision = this.getShape().collisionWith(physicsEntity.getShape());
		if(shapeCollision.isPresent())
		{
			PhysCollision pc = new DefaultPhysCollision(this, physicsEntity, shapeCollision.get());
			return Optional.of(pc);
		} else return Optional.absent();
	}

	/**
	 * Draws this shape on the Graphics object. Should use the pixel position and size of the shape to figure out where
	 * to draw.
	 *
	 * @param g
	 * 		The Graphics object this shape must be drawn upon.
	 */
	@Override
	public void draw(Graphics2D g)
	{
		if(!this.hasProperty(":hasSprite") || !(Boolean) this.getProperty(":hasSprite")) this.getShape().draw(g);
		else ((Drawable) this.getProperty(":sprite")).draw(g);
		super.draw(g);
	}

	/**
	 * @return The current angle of this {@code PhysCollision}, in radians, relative to the x axis
	 */
	@Override
	public double getAngle()
	{
		return this.rdl.getDerivative(0);
	}

	/**
	 * @return The center position of this Drawable object, in a two-dimensional point.
	 */
	@Override
	public Dimensional getCenterPosition()
	{
		return this.pdl.getDerivative(0);
	}

	/**
	 * Used by the {@code PhysCollision} class. TODO
	 *
	 * @return The square root of the coefficient of dynamic friction.
	 */
	@Override
	public double getCoefficientOfDynamicFrictionSqrt()
	{
		return this.coefficientOfDynamicFrictionSqrt;
	}

	/**
	 * Used by the {@code PhysCollision} class; since the coefficient of restitution for a collision is calculated using
	 * {@code sqrt(r_1*r_2)}, it is more efficient for a {@code PhysicsEntity} to remember the square root of its
	 * restitution since {@code sqrt(r_1*r_2)=sqrt(r_1)*sqrt(r_2)}.
	 *
	 * @return The square root of the coefficient of restitution
	 */
	@Override
	public double getCoefficientOfRestitutionSqrt()
	{
		return this.coefficientOfRestitutionSqrt;
	}

	/**
	 * Used by the {@code PhysCollision} class. TODO
	 *
	 * @return The square root of the coefficient of static friction.
	 */
	@Override
	public double getCoefficientOfStaticFrictionSqrt()
	{
		return this.coefficientOfStaticFrictionSqrt;
	}

	/**
	 * @return The Color of this object
	 */
	@Override
	public Color getColor()
	{
		return this.getShape().getColor();
	}

	/**
	 * @param i
	 * 		Which derivative to return, as a nonnegative integer
	 *
	 * @return That derivative of movement
	 */
	Vector getDerivative(int i)
	{
		return this.pdl.getDerivative(i);
	}

	/**
	 * @return The height of this object. Depending on time, may be in pixels or game units.
	 */
	@Override
	public double getHeight()
	{
		return this.getShape().getHeight();
	}

	/**
	 * @return The mass of this {@code PhysicsEntity}
	 */
	@Override
	public double getMass()
	{
		return this.mass;
	}

	/**
	 * The mass moment of inertia of this {@code PhysicsEntity}, calculated by multiplying the moment of inertia of the
	 * {@code PhysicsEntity}'s shape, and its mass.
	 *
	 * @return The moment of inertia of this {@code PhysicsEntity}
	 */
	@Override
	public double getMomentOfInertia()
	{
		return this.getShape().momentOfInertia() * this.getMass();
	}

	/**
	 * Returns the value of the property of a given name. Should return {@code null}, if that property is not set. Certain
	 * properties should always return particular things, having been internally set:
	 * - !universe -> The {@code Universe} in which this {@code Entity} resides
	 * - !self -> This {@code Entity}
	 *
	 * @param ofName
	 * 		The name of a property
	 *
	 * @return The value of the property named {@code ofName}
	 */
	@Override
	public Object getProperty(String ofName)
	{
		switch(ofName)
		{
			case ":width":
				return this.getWidth();
			case ":height":
				return this.getHeight();
			case ":rotation":
				return this.getAngle();
			case ":centerPosition":
				return this.getCenterPosition();
			case ":color":
				return this.getColor();
			case ":mass":
				return this.getMass();
			case ":coefficientOfDynamicFrictionSqrt":
				return this.getCoefficientOfDynamicFrictionSqrt();
			case ":coefficientOfStaticFrictionSqrt":
				return this.getCoefficientOfStaticFrictionSqrt();
			case ":coefficientOfRestitutionSqrt":
				return this.getCoefficientOfRestitutionSqrt();
			case ":momentOfInertia":
				return this.getMomentOfInertia();
			case ":shape":
				return this.getShape();
			case ":angle":
				return this.getAngle();
			case ":velocity":
				return this.getVelocity();
			default:
				if((ofName.length() > 5) && ":deriv".equals(ofName.substring(0, 5)))
					this.getDerivative(Integer.valueOf(ofName.substring(5)));
				else if((ofName.length() > 6) && ":rderiv".equals(ofName.substring(0, 6)))
					this.getRotationalDerivative(Integer.valueOf(ofName.substring(6)));
				return super.getProperty(ofName);
		}
	}

	/**
	 * @param i
	 * 		Which derivative to return, as a nonnegative integer
	 *
	 * @return The {@code i}th rotational derivative of this {@code PhysicsEntity}
	 */
	double getRotationalDerivative(int i)
	{
		return this.rdl.getDerivative(i);
	}

	/**
	 * TODO: counterclockwise, I think
	 *
	 * @return The current rotational velocity of this {@code PhysicsEntity}, in radians per second
	 */
	@Override
	public double getRotationalVelocity()
	{
		return this.rdl.getDerivative(1);
	}

	/**
	 * The shape of this {@code PhysicsEntity}. Guaranteed to entirely contain this {@code PhysicsEntity} minimally.
	 *
	 * @return The shape of this {@code PhysicsEntity}
	 */
	@Override
	public Collidable getShape()
	{
		return this.shape;
	}

	/**
	 * @return The {@code Universe} in which this {@code Entity} resides
	 */
	private Universe getUniverse()
	{
		return (Universe) this.getProperty("!universe");
	}

	/**
	 * @return The velocity of this {@code PhysicsEntity}, as a {@code Vector} representing both direction and magnitude
	 */
	@Override
	public Vector getVelocity()
	{
		return this.getDerivative(1);
	}

	/**
	 * @return The width of this object. Depending on time, may be in pixels or game units.
	 */
	@Override
	public double getWidth()
	{
		return this.getShape().getWidth();
	}

	/**
	 * Initializes the immutable properties that {@code PhysicsEntity}s are required to have.
	 */
	private void initDefaultProperties()
	{
		if(!super.hasProperty(":shape")) throw new PropertyMisformatException(SHAPE_MISFORMAT_EXCEPTION);
		this.shape = (Collidable) super.getProperty(":shape");

		if(!super.hasProperty(":mass")) throw new PropertyMisformatException(MASS_MISFORMAT_EXCEPTION);
		this.mass = (Double) super.getProperty(":mass");

		if(!super.hasProperty(":coefficientOfDynamicFrictionSqrt"))
			throw new PropertyMisformatException(COEFFICIENT_DYNAMIC_MISFORMAT_EXCEPTION);
		this.coefficientOfDynamicFrictionSqrt = (Double) super.getProperty(":coefficientOfDynamicFrictionSqrt");

		if(!super.hasProperty(":coefficientOfStaticFrictionSqrt"))
			throw new PropertyMisformatException(COEFFICIENT_STATIC_MISFORMAT_EXCEPTION);
		this.coefficientOfStaticFrictionSqrt = (Double) super.getProperty(":coefficientOfStaticFrictionSqrt");

		if(!super.hasProperty(":coefficientOfRestitutionSqrt"))
			throw new PropertyMisformatException(COEFFICIENT_RESTITUTION_MISFORMAT_EXCEPTION);
		this.coefficientOfRestitutionSqrt = (Double) super.getProperty(":coefficientOfRestitutionSqrt");
	}

	/**
	 * Adds the {@code Input}s required by the {@code PhysicsEntity} interface
	 */
	private void initInputs()
	{
		this.inputs.put("!doApplyForce", Inputs.doApplyForce(this));
		this.inputs.put("!doApplyImpulse", Inputs.doApplyImpulse(this));
		this.inputs.put("!doPhysicalRemove", Inputs.doPhysicalRemove(this));
		this.inputs.put("!doRotate", Inputs.doRotate(this));
		this.inputs.put("!removeFromForceGroup", Inputs.removeFromForceGroup(this));
		this.inputs.put("!addToForceGroup", Inputs.addToForceGroup(this));
		this.inputs.put("!removeFromCollisionGroup", Inputs.removeFromCollisionGroup(this));
		this.inputs.put("!addToCollisionGroup", Inputs.addToCollisionGroup(this));
		this.inputs.put("!addToBoundary", Inputs.addToBoundary(this));
		this.inputs.put("!removeFromBoundary", Inputs.removeFromBoundary(this));
		this.inputs.put("!addToRayGroup", Inputs.addToRayGroup(this));
		this.inputs.put("!removeFromRayGroup", Inputs.removeFromRayGroup(this));
	}

	/**
	 * @return Whether or not this {@code PhysicsEntity} is being represented by a sprite
	 */
	private boolean isSprite()
	{
		return this.hasProperty(":isSprite") && (Boolean) this.getProperty(":isSprite");
	}

	/**
	 * An action to be performed continuously. In order to allow for time-related actions to be performed correctly, the
	 * amount of time between the end of the last 'tick' and the beginning of the current 'tick' is provided.
	 *
	 * @param nanosSinceLastTick
	 * 		The number of nanoseconds between the end of the last tick and the beginning of the current tick.
	 */
	@Override
	public void onTick(long nanosSinceLastTick)
	{
		if(this.hasProperty(":animated") && (Boolean) this.getProperty(":animated"))
			((AnimatedSprite) this.getProperty(":sprite")).tick(nanosSinceLastTick);
		if(this.mass > 0)
		{
			this.setVelocity(this.getVelocity().plus(this.forces.smult(nanosSinceLastTick / ONE_SECOND / this.mass))
								 .plus(this.impulses.sdiv(this.getMass())));
			if(this.getMomentOfInertia() > 0)
			{
				this.setRotationalVelocity(this.getRotationalVelocity() +
						((this.torque * nanosSinceLastTick) / ONE_SECOND / this.getMomentOfInertia()) + (
						this.rotationalImpulse / this.getMomentOfInertia()));
			}
		}
		this.pdl.step(nanosSinceLastTick);
		this.rdl.step(nanosSinceLastTick);

		this.getShape().setCenterPosition(this.getCenterPosition());
		this.getShape().setRotation(this.getAngle());

		this.forces = Vector.ZERO_2D;
		this.impulses = Vector.ZERO_2D;

		this.rotationalImpulse = 0.0;
		this.torque = 0.0;

		super.onTick(nanosSinceLastTick);
	}

	/**
	 * Attaches the value of {@code ofValue} to the name {@code ofName} within this {@code Entity}. The value {@code null}
	 * must be identical to removing the property. The following properties are internally set and calling putProperty
	 * with them should have no effect:
	 * - !universe -> Should always refer to the {@code Universe} in which this {@code Entity} resides
	 * - !self -> Should always refer to this {@code Entity}
	 *
	 * @param ofName
	 * 		The name of a property
	 * @param ofValue
	 * 		Any object
	 */
	@Override
	public void putProperty(String ofName, Object ofValue)
	{
		switch(ofName)
		{
			case ":width":
				this.setWidth((Double) ofValue);
				break;
			case ":height":
				this.setHeight((Double) ofValue);
				break;
			case ":rotation":
				this.setAngle((Double) ofValue);
				break;
			case ":centerPosition":
				this.setCenterPosition((Dimensional) ofValue);
				break;
			case ":color":
				this.setColor((Color) ofValue);
				break;
			case "!momentOfInertia":
				throw new PropertyMisformatException(MOMENT_OF_INERTIA_MISFORMAT_EXCEPTION);
			case ":mass":
				this.setMass((Double) ofValue);
				break;
			case ":coefficientOfDynamicFrictionSqrt":
				this.coefficientOfDynamicFrictionSqrt = (Double) ofValue;
				break;
			case ":coefficientOfStaticFrictionSqrt":
				this.coefficientOfStaticFrictionSqrt = (Double) ofValue;
				break;
			case ":coefficientOfRestitutionSqrt":
				this.coefficientOfRestitutionSqrt = (Double) ofValue;
				break;
			case ":shape":
				this.shape = (Collidable) ofValue;
				break;
			case ":angle":
				this.setAngle((Double) ofValue);
				break;
			case ":forceGroups":
				this.getUniverse().removeFromAllForceGroups(this);
				for(Double f : (List<Double>) ofValue)
					this.getUniverse().addToForceGroup(this, this.getUniverse().getForceGroupByNumber(f.intValue()));
				super.putProperty(ofName, ofValue);
				break;
			case ":collisionGroups":
				this.getUniverse().removeFromAllCollisionGroups(this);
				for(Double f : (List<Double>) ofValue)
					this.getUniverse()
						.addToCollisionGroup(this, this.getUniverse().getCollisionGroupByNumber(f.intValue()));
				super.putProperty(ofName, ofValue);
				break;
			case ":rayGroups":
				this.getUniverse().removeFromAllRayGroups(this);
				for(Double f : (List<Double>) ofValue)
					this.getUniverse().addToRayGroup(this, this.getUniverse().getRayGroupByNumber(f.intValue()));
				super.putProperty(ofName, ofValue);
				break;
			case ":boundaryGroups":
				this.getUniverse().removeAllBoundariesFrom(this);
				for(Double f : (List<Double>) ofValue)
					this.getUniverse().applyBoundaryTo(this.getUniverse().getBoundaryByNumber(f.intValue()), this);
				super.putProperty(ofName, ofValue);
				break;
			case ":velocity":
				this.setVelocity((Dimensional) ofValue);
				break;
			default:
				if((ofName.length() > 5) && ":deriv".equals(ofName.substring(0, 5)))
					this.setDerivative(Integer.valueOf(ofName.substring(5)), (Dimensional) ofValue);
				else if((ofName.length() > 6) && ":rderiv".equals(ofName.substring(0, 6)))
					this.setRotationalDerivative(Integer.valueOf(ofName.substring(6)), (Double) ofValue);
				else super.putProperty(ofName, ofValue);
				break;
		}
	}

	/**
	 * Whenever a {@code PhysCollision} is created by {@code collisionWith(PhysicsEntity)}, this method causes the overlap
	 * between this {@code PhysicsEntity} and another to be undone using the MTV. Depending on {@code reactionType},
	 * this method may also enforce friction, impulse, or both.
	 *
	 * @param collision
	 * 		A {@code PhysCollision} between this {@code PhysicsEntity} and another {@code PhysicsEntity}
	 * @param reactionType
	 * 		A {@code ReactionType}, dealing with any of several types of reactions.
	 */
	@Override
	public void react(PhysCollision collision, ReactionType reactionType)
	{
		boolean impulses = reactionType == ReactionType.FRICTION_AND_IMPULSE
				|| reactionType == ReactionType.IMPULSE_ONLY;
		boolean friction = reactionType == ReactionType.FRICTION_AND_IMPULSE
				|| reactionType == ReactionType.FRICTION_ONLY;

		this.doInput("doCollide", NMaps.of(Pair.tuple("!collision", (Evaluator) new Constant(collision)),
				Pair.tuple("!doimpulse", new Constant(impulses)), Pair.tuple("!dofriction", new Constant(friction)),
				Pair.tuple("!mtv", new Constant(collision.getMTV())),
				Pair.tuple("!impulse", new Constant(collision.getImpulse())),
				Pair.tuple("!friction", new Constant(collision.getSlidingFrictionalImpulse())),
				Pair.tuple("!collisionPoint", new Constant(collision.getCollisionPoint())),
				Pair.tuple("!collidingWith", new Constant(collision.getOther().getProperty(":name").toString()))));
		if(collision.getMTV().mag2() == 0) return;
		this.applyLocationChange(collision.getMTV());

		if(impulses)
		{
			this.applyImpulseAt(collision.getImpulse(), collision.getCollisionPoint());
			if(friction)
			{
				this.applyImpulseAt(collision.getSlidingFrictionalImpulse(), this.getCenterPosition());
			}
		}
	}

	/**
	 * Adds to the current angle of this {@code PhysicsEntity}
	 * TODO counterclockwise?
	 *
	 * @param plusTheta
	 * 		How much to add to the current angle of this {@code PhysicsEntity}
	 */
	@Override
	public void rotate(double plusTheta)
	{
		this.setAngle((this.getAngle() + plusTheta) % (Math.PI * 2));
	}

	/**
	 * Changes the angle of this {@code PhysCollision} relative to the x-axis, in radians.
	 *
	 * @param theta
	 * 		An angle from the x-axis, in radians
	 */
	@Override
	public void setAngle(double theta)
	{
		this.rdl.setDerivative(0, theta);
		this.getShape().setRotation(this.rdl.getDerivative(0));
	}

	/**
	 * @param centerPosition
	 * 		The next center position of this Drawable object, in a two-dimensional point.
	 */
	@Override
	public void setCenterPosition(Dimensional centerPosition)
	{
		this.getShape().setCenterPosition(centerPosition);
		this.pdl.setDerivative(0, new Vector(centerPosition));
		if(this.isSprite()) ((Drawable) this.getProperty(":sprite")).setCenterPosition(centerPosition);
	}

	/**
	 * @param c
	 * 		The new Color of this object
	 */
	@Override
	public void setColor(Color c)
	{
		this.getShape().setColor(c);
	}

	/**
	 * @param i
	 * 		The derivative of position to set
	 * @param dim
	 * 		The value to set
	 */
	void setDerivative(int i, Dimensional dim)
	{
		if(i <= 0) throw new IllegalArgumentException("Cannot set a non-derivative of position from here.");
		this.pdl.setDerivative(i, new Vector(dim));
	}

	/**
	 * @param h
	 * 		The new height of this object
	 */
	@Override
	public void setHeight(double h)
	{
		this.getShape().setHeight(h);
		if(this.isSprite()) ((Drawable) this.getProperty(":sprite")).setHeight(h);
	}

	/**
	 * Sets the mass of this {@code PhysicsEntity}
	 *
	 * @param newMass
	 * 		A new mass for this {@code PhysicsEntity}
	 */
	@Override
	public void setMass(double newMass)
	{
		this.mass = newMass;
	}

	/**
	 * @param i
	 * 		The derivative of rotation to set
	 * @param rotationalDerivative
	 * 		The value to set
	 */
	void setRotationalDerivative(int i, double rotationalDerivative)
	{
		this.rdl.setDerivative(i, rotationalDerivative);
	}

	/**
	 * Sets the rotational velocity of this {@code PhysicsEntity}
	 *
	 * @param rotationalVelocity
	 * 		A rotational velocity, in radians per second
	 */
	@Override
	public void setRotationalVelocity(double rotationalVelocity)
	{
		this.rdl.setDerivative(1, rotationalVelocity);
	}

	/**
	 * Changes the velocity of this {@code PhysicsEntity}
	 *
	 * @param newVelocity
	 * 		A {@code Dimensional} representing the new velocity of this {@code PhysicsEntity}
	 */
	@Override
	public void setVelocity(Dimensional newVelocity)
	{
		this.pdl.setDerivative(1, new Vector(newVelocity));
	}

	/**
	 * @param w
	 * 		The new width of this object
	 */
	@Override
	public void setWidth(double w)
	{
		this.getShape().setWidth(w);
		if(this.isSprite()) ((Drawable) this.getProperty(":sprite")).setWidth(w);
	}

	@Override
	public String toString()
	{
		return "AbstractPhysicsEntity{" +
				"coefficientOfDynamicFrictionSqrt=" + this.coefficientOfDynamicFrictionSqrt +
				", coefficientOfRestitutionSqrt=" + this.coefficientOfRestitutionSqrt +
				", coefficientOfStaticFrictionSqrt=" + this.coefficientOfStaticFrictionSqrt +
				", forces=" + this.forces +
				", impulses=" + this.impulses +
				", mass=" + this.mass +
				", pdl=" + this.pdl +
				", rdl=" + this.rdl +
				", rotationalImpulse=" + this.rotationalImpulse +
				", shape=" + this.shape +
				", torque=" + this.torque +
				'}';
	}

	/**
	 * @return The x Interval of this {@code PhysicsEntity}
	 */
	@Override
	public Interval xInterval()
	{
		return this.getShape().xInterval();
	}

	/**
	 * @return The y Interval of this {@code PhysicsEntity}
	 */
	@Override
	public Interval yInterval()
	{
		return this.getShape().yInterval();
	}
}
