package uk.co.praguematica.urlmapping.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import uk.co.praguematica.urlmapping.handlers.*;
import uk.co.praguematica.urlmapping.serializers.Format;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
	public String value() default "";
	public Class<? extends ResponseHandler> responseHandler() default DefaultHandler.class;
	public String responseFormat() default Format.UNSPECIFIED;
	public String contentType() default "text/plain";
	public String fileName() default "";
	public String encoding() default "UTF-8";
	public Class<? extends SecurityHandler> securityHandler() default DefaultSecurityHandler.class;

}
