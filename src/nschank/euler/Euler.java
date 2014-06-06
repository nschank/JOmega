package nschank.euler;

import java.lang.annotation.Documented;


@Documented
public @interface Euler
{
	int problem() default 0;
	String name() default "Unknown";
}
