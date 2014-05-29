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
 * automatically call a corresponding Output starting with the "on" keyword.
 *
 * Another central interface in the nschank.engn.play package.
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
	 * Causes an Input to be run given the type name and the arguments (as a map from argument name to evaluatable
	 * expression). An argument is not guaranteed to be used or evaluated, if the input does not use it by name. The
	 * input should be prevented from running if the "enabled" property, if it exists, evaluates to false. If the input
	 * name starts with "do", then the corresponding output starting with "on" must be called.
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
	 * Returns the value of the property of a given name. Should return {@code null}, if that property is not set.
	 * @param ofName
	 *		The name of a property
	 * @return The value of the property named {@code ofName}
	 */
	Object getProperty(String ofName);
	/**
	 * Checks whether there is an {@code Input} registered under the name {@code ofName}
	 * @param ofName
	 *		The name of a possible Input
	 * @return Whether calling that {@code Input} will run a registered {@code Input} rather than returning immediately
	 */
	boolean hasInput(String ofName);
	/**
	 * Checks whether this is a property registered under the name {@code ofName}
	 * @param ofName
	 *		The name of a possible property
	 * @return Whether asking for the property named {@code ofName} will return an {@code Object}, rather than just
	 * 		{@code null}
	 */
	boolean hasProperty(String ofName);
	/**
	 * Registers an {@code Input} to occur whenever the name {@code ofName} is called. Will replace any existing Input
	 * of the name {@code ofName} that the {@code Entity} already has registered.
	 * @param ofName
	 * 		The name of an {@code Input}
	 * @param reaction
	 * 		An {@code Input} that will be run on the name {@code ofName}
	 */
	void putInput(String ofName, Input reaction);
	/**
	 * Calls putProperty for each mapping of a String to an Object in the given map of properties
	 * @param properties
	 * 		Any mapping of String (names) to Object (values)
	 */
	void putProperties(Map<String, Object> properties);
	/**
	 * Attaches the value of {@code ofValue} to the name {@code ofName} within this {@code Entity}. The value {@code null}
	 * must be identical to removing the property.
	 * @param ofName
	 * 		The name of a property
	 * @param ofValue
	 * 		Any object
	 */
	void putProperty(String ofName, Object ofValue);
	/**
	 * Removes the property {@code ofName} from the properties which this {@code Entity} holds on to. If the name does not
	 * correspond to a property, does nothing.
	 * @param ofName
	 * 		The name of a property
	 *
	 */
	void removeProperty(String ofName);
}
