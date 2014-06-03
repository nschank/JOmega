package nschank.engn.play;

import nschank.engn.play.io.Connection;
import nschank.engn.play.io.Input;
import nschank.engn.play.io.eval.Evaluator;
import nschank.engn.shape.Drawable;

import java.util.Map;


/**
 * Created by Nicolas Schank for package nschank.engn.play
 * Created on 21 Oct 2013
 * Last updated on 29 May 2014
 *
 * Another central interface in the nschank.engn.play package.
 *
 * An Entity is something that exists inside the game world; any character, object, or even invisible interactions are
 * types of Entities. Entities have properties, inputs, and outputs.
 *
 * Properties are self explanatory, and are essentially a hashmap from String (property name) to Object underlying the
 * Entity. Properties have a few special characteristics, covered in putProperty and getProperty, which basically relate
 * to special naming rules.
 *
 * Outputs are the way that an Entity interacts with the World. They are represented by a String description (which also
 * has special naming rules) that are often called by the object internally in some way. The word "on" often proceeds an
 * Output description: e.g. onTouch, onPress, onOpen.
 *
 * Inputs are the way that the World, or other Entities, interact with an Entity. They too are represented by a String
 * description, and are often called by a Connection to another Entity's output, or called directly by a user interaction.
 * They often start with the "do" keyword: e.g. doJump, doOpen, doSomething. An Input starting with the "do" keyword must
 * automatically call a corresponding Output starting with the "on" keyword; this is also true of Inputs starting with
 * "!do", though it should still only be replaced by "on". (For example "!doRemove" calls "onRemove")
 *
 * Entities have certain default properties, {@code Output}s, and {@code Input}s that must be honoured internally.
 *
 * The following properties (marked with a starting exclamation mark) must be immutable and follow the given definitions:
 * - !self 		->	This {@code Entity}
 * - !universe 	-> 	The {@code Universe} in which this {@code Entity} lives.
 *
 * The following {@code Input}s must not be replaceable and must take the following actions:
 * - !errorCheckPrint	->	Used for error checking. Prints every argument/value pair that was input to it.
 * - !doRemove			->	Removes this {@code Entity} from its {@code Universe}.
 * - !removeProperty	->	For the String value of the argument name "property", remove this {@code Entity}'s property
 * 							of that name.
 * - !runOutput			->	For the String value of the argument name "target", runs this {@code Entity}'s output of that
 * 							name. All arguments besides "target" should be passed along to that output.
 * - !setProperty		->	For every argument name/value pair, set this {@code Entity}'s property of that name to that
 * 							value.
 *
 * The following {@code Output}s must be used in the following manners, with the following arguments:
 * - !onDraw			->	Must be called by the draw(Graphics2D) method, with no arguments. Should not (and literally
 * 							cannot) attempt to draw anything directly.
 * - !onTick			->	Must be called by the onTick(long) method, with one argument: nanosSinceLastTick->that long.
 *
 * @author nschank, Brown University
 * @version 4.6
 */
public interface Entity extends Tickable, Drawable
{
	/**
	 * Connects an output of the given name to an input using the given connection.
	 *
	 * @param output
	 * 		The name of an Output to connect to another Entity's Input
	 */
	void connect(String output, Connection conn);
	/**
	 * Connects an {@code Output} of the given name to an {@code Input} from another {@code Entity}, returning the
	 * {@code Connection} created
	 *
	 * @param output
	 * 		The name of an {@code Output} to connect to another Entity's Input
	 * @param connectionEntity
	 * 		The {@code Entity} to connect this {@code Output} to
	 * @param connectedInput
	 * 		The {@code Input} to connect this {@code Output} to
	 *
	 * @return The {@code Connection} created
	 */
	Connection connect(String output, Entity connectionEntity, String connectedInput);
	/**
	 * Causes an Input to be run given the type name and the arguments (as a map from argument name to evaluatable
	 * expression). An argument is not guaranteed to be used or evaluated, if the input does not use it by name. The
	 * input should be prevented from running if the "enabled" property, if it exists, evaluates to false. If the input
	 * name starts with "do", then the corresponding output starting with "on" must be called. The input is guaranteed
	 * to be performed first.
	 *
	 * @param inputType
	 * 		The name. Often starts with "do" and, if it does, an output will be fired.
	 * @param arguments
	 * 		An argument name/value pairing map
	 */
	void doInput(String inputType, Map<String, Evaluator> arguments);
	/**
	 * @return All properties of this Entity as a map
	 */
	Map<String, Object> getProperties();
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
	Object getProperty(String ofName);
	/**
	 * Checks whether there is an {@code Input} registered under the name {@code ofName}
	 *
	 * @param ofName
	 * 		The name of a possible Input
	 *
	 * @return Whether calling that {@code Input} will run a registered {@code Input} rather than returning immediately
	 */
	boolean hasInput(String ofName);
	/**
	 * Checks whether this is a property registered under the name {@code ofName}.
	 *
	 * @param ofName
	 * 		The name of a possible property
	 *
	 * @return Whether asking for the property named {@code ofName} will return an {@code Object}, rather than just
	 * {@code null}
	 */
	boolean hasProperty(String ofName);
	/**
	 * Registers an {@code Input} to occur whenever the name {@code ofName} is called. Will replace any existing Input
	 * of the name {@code ofName} that the {@code Entity} already has registered.
	 *
	 * @param ofName
	 * 		The name of an {@code Input}
	 * @param reaction
	 * 		An {@code Input} that will be run on the name {@code ofName}
	 */
	void putInput(String ofName, Input reaction);
	/**
	 * Calls putProperty for each mapping of a String to an Object in the given map of properties
	 *
	 * @param properties
	 * 		Any mapping of String (names) to Object (values)
	 */
	void putProperties(Map<String, Object> properties);
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
	void putProperty(String ofName, Object ofValue);
	/**
	 * Removes the property {@code ofName} from the properties which this {@code Entity} holds on to. If the name does not
	 * correspond to a property, does nothing.
	 *
	 * @param ofName
	 * 		The name of a property
	 */
	void removeProperty(String ofName);
	/**
	 * Runs the output of the given name using the given arguments.
	 *
	 * @param ofName
	 * 		The name of an Output
	 * @param args
	 * 		Arguments, as String/Evaluator pairs, to supply to that Output
	 */
	void runOutput(String ofName, Map<String, Evaluator> args);
}
