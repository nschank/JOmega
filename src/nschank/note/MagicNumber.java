package nschank.note;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author Nicolas Schank
 * @version 2013 05 20
 * @since 2013 05 20 4:22 PM
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.LOCAL_VARIABLE, ElementType.FIELD})
public @interface MagicNumber
{
	public int[] number() default {-1};
	public abstract String[] reason();
}
