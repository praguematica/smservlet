package uk.co.praguematica.urlmapping.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import uk.co.praguematica.urlmapping.handlers.ResponseHandler;
import uk.co.praguematica.urlmapping.handlers.DefaultHandler;
import uk.co.praguematica.urlmapping.serializers.Format;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SelfMapped {
	public Class<? extends ResponseHandler> responseHandler() default DefaultHandler.class;
	public String responseFormat() default Format.UNSPECIFIED;
}
