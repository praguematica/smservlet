package co.uk.praguematica.urlmapping.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import co.uk.praguematica.urlmapping.handlers.ResponseHandler;
import co.uk.praguematica.urlmapping.handlers.DefaultHandler;
import co.uk.praguematica.urlmapping.serializers.Format;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SelfMapped {
	public Class<? extends ResponseHandler> responseHandler() default DefaultHandler.class;
	public String responseFormat() default Format.UNSPECIFIED;
}
