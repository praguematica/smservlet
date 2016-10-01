package co.uk.praguematica.urlmapping.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import co.uk.praguematica.urlmapping.serializers.Format;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Attribute {
	String value() default "";	
	String format() default Format.STRING;
}
