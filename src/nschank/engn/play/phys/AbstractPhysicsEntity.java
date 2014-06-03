package nschank.engn.play.phys;

import com.google.common.base.Optional;
import nschank.collect.dim.Dimensional;
import nschank.collect.dim.Vector;
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
 * @version 4.8
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
	private PositionDerivativeList2df pdl;
	private RotationDerivativeListf rdl;
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
		this.pdl = new PositionDerivativeList2df(this.getShape().getCenterPosition());
		this.rdl = new RotationDerivativeListf(0.0f);
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
	 * @param change
	 */
	@Override
	public void applyLocationChange(Vector change)
	{
		this.setCenterPosition(change.plus(this.getShape().getCenterPosition());
	}

	/**
	 *
	 */
	@Override
	public void arrestMotion()
	{
		this.pdl = new PositionDerivativeList2df(this.getCenterPosition());
		this.rdl = new RotationDerivativeListf(this.getAngle());
	}

	/**
	 * @param ray
	 * 		A {@code Ray} which may be colliding with this {@code PhysicsEntity}
	 *
	 * @return
	 */
	@Override
	public Optional<Double> collisionWith(Ray ray)
	{
		return this.getShape().distanceAlong(ray);
	}

	/**
	 * @param physicsEntity
	 * 		Another {@code PhysicsEntity} which may be colliding with this one
	 *
	 * @return
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
	 * @param g
	 */
	@Override
	public void draw(Graphics2D g)
	{
		if(!this.hasProperty(":hasSprite") || !(Boolean) this.getProperty(":hasSprite")) this.getShape().draw(g);
		else ((Drawable) this.getProperty(":sprite")).draw(g);
		super.draw(g);
	}

	/**
	 * @return
	 */
	@Override
	public double getAngle()
	{
		return this.rdl.getAngle();
	}

	/**
	 * @param theta
	 */
	@Override
	public void setAngle(double theta)
	{
		this.rdl.setAngle(theta);
		this.getShape().setRotation(this.rdl.getAngle());
	}

	/**
	 * @return
	 */
	@Override
	public Dimensional getCenterPosition()
	{
		return this.pdl.getPosition();
	}

	/**
	 * @param centerPosition
	 */
	@Override
	public void setCenterPosition(Dimensional centerPosition)
	{
		this.getShape().setCenterPosition(centerPosition);
		this.pdl.setPosition(centerPosition);
		if(this.isSprite()) ((Drawable) this.getProperty(":sprite")).setCenterPosition(centerPosition);
	}

	/**
	 * @return
	 */
	@Override
	public double getCoefficientOfDynamicFrictionSqrt()
	{
		return this.coefficientOfDynamicFrictionSqrt;
	}

	/**
	 * @return
	 */
	@Override
	public double getCoefficientOfRestitutionSqrt()
	{
		return this.coefficientOfRestitutionSqrt;
	}

	/**
	 * @return
	 */
	@Override
	public double getCoefficientOfStaticFrictionSqrt()
	{
		return this.coefficientOfStaticFrictionSqrt;
	}

	/**
	 * @return
	 */
	@Override
	public Color getColor()
	{
		return this.getShape().getColor();
	}

	/**
	 * @param c
	 */
	@Override
	public void setColor(Color c)
	{
		this.getShape().setColor(c);
	}

	/**
	 * @param i
	 *
	 * @return
	 */
	Vector getDerivative(int i)
	{
		return this.pdl.getDerivative(i);
	}

	/**
	 * @return
	 */
	@Override
	public double getHeight()
	{
		return this.getShape().getHeight();
	}

	/**
	 * @param h
	 */
	@Override
	public void setHeight(double h)
	{
		this.getShape().setHeight(h);
		if(this.isSprite()) ((Drawable) this.getProperty(":sprite")).setHeight(h);
	}

	/**
	 * @return
	 */
	@Override
	public double getMass()
	{
		return this.mass;
	}

	/**
	 * @param newMass
	 */
	@Override
	public void setMass(double newMass)
	{
		this.mass = newMass;
	}

	/**
	 * @return
	 */
	@Override
	public double getMomentOfInertia()
	{
		return this.getShape().momentOfInertia() * this.getMass();
	}

	/**
	 * @param ofName
	 * 		The name of a property
	 *
	 * @return
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
	 *
	 * @return
	 */
	double getRotationalDerivative(int i)
	{
		return this.rdl.getDerivative(i);
	}

	/**
	 * @return
	 */
	@Override
	public double getRotationalVelocity()
	{
		return this.rdl.getVelocity();
	}

	/**
	 * @param rotationalVelocity
	 */
	@Override
	public void setRotationalVelocity(double rotationalVelocity)
	{
		this.rdl.setVelocity(rotationalVelocity);
	}

	/**
	 * @return
	 */
	@Override
	public Collidable getShape()
	{
		return this.shape;
	}

	/**
	 * @return
	 */
	private Universe getUniverse()
	{
		return (Universe) this.getProperty("!universe");
	}

	/**
	 * @return
	 */
	@Override
	public Vector getVelocity()
	{
		return this.getDerivative(1);
	}

	/**
	 * @param newVelocity
	 */
	@Override
	public void setVelocity(Dimensional newVelocity)
	{
		this.pdl.setVelocity(newVelocity);
	}

	/**
	 * @return
	 */
	@Override
	public double getWidth()
	{
		return this.getShape().getWidth();
	}

	/**
	 * @param w
	 */
	@Override
	public void setWidth(double w)
	{
		this.getShape().setWidth(w);
		if(this.isSprite()) ((Drawable) this.getProperty(":sprite")).setWidth(w);
	}

	/**
	 *
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
	 * @return
	 */
	private boolean isSprite()
	{
		return this.hasProperty(":isSprite") && (Boolean) this.getProperty(":isSprite");
	}

	/**
	 * @param nanosSinceLastTick
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
	 * @param ofName
	 * 		The name of a property
	 * @param ofValue
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
	 * @param plusTheta
	 */
	@Override
	public void rotate(double plusTheta)
	{
		this.setAngle((this.getAngle() + plusTheta) % (Math.PI * 2));
	}

	/**
	 * @param i
	 * @param vec2f
	 */
	void setDerivative(int i, Dimensional vec2f)
	{
		if(i <= 0) throw new IllegalArgumentException("Cannot set a non-derivative of position from here.");
		this.pdl.setDerivative(i, vec2f);
	}

	/**
	 * @param i
	 * @param rotationalDerivative
	 */
	void setRotationalDerivative(int i, double rotationalDerivative)
	{
		this.rdl.setDerivative(i, rotationalDerivative);
	}

	/**
	 * @return
	 */
	@Override
	public Interval xInterval()
	{
		return this.getShape().xInterval();
	}

	/**
	 * @return
	 */
	@Override
	public Interval yInterval()
	{
		return this.getShape().yInterval();
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
}
