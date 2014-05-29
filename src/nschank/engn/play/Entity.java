package nschank.engn.play;

import com.google.common.base.Optional;
import nschank.engn.play.io.Input;
import nschank.engn.play.io.Output;
import nschank.engn.play.io.eval.Evaluator;
import nschank.engn.shape.Drawable;

import java.util.Map;


/**
 * Created by Nicolas Schank for package nschank.engn.play
 * Created on 21 Oct 2013
 * Last updated on 29 May 2014
 *
 * @author nschank, Brown University
 * @version 4.6
 */
public interface Entity extends Tickable, Drawable
{
	/**
	 * @param inputType
	 * @param input
	 */
	void doInput(String inputType, Map<String, Evaluator> input);
	/**
	 * @param ofName
	 *
	 * @return
	 */
	Optional<Input> getInput(String ofName);
	/**
	 * @param ofName
	 *
	 * @return
	 */
	Output getOutput(String ofName);
	/**
	 * @return
	 */
	Map<String, Object> getProperties();
	/**
	 * @param ofName
	 *
	 * @return
	 */
	Object getProperty(String ofName);
	/**
	 * @param ofName
	 *
	 * @return
	 */
	boolean hasInput(String ofName);
	/**
	 * @param ofName
	 *
	 * @return
	 */
	boolean hasProperty(String ofName);
	/**
	 * @param ofName
	 * @param reaction
	 */
	void putInput(String ofName, Input reaction);
	/**
	 * @param properties
	 */
	void putProperties(Map<String, Object> properties);
	/**
	 * @param ofName
	 * @param ofValue
	 */
	void putProperty(String ofName, Object ofValue);
	/**
	 * @param ofName
	 */
	void removeProperty(String ofName);
}
