package com.dvwsolutions.urlmapping.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.dvwsolutions.urlmapping.handlers.ResponseHandler;
import com.dvwsolutions.urlmapping.handlers.ResponseHandlerDefaultImpl;
import com.dvwsolutions.urlmapping.serializers.Format;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
	public String value() default "";
	public Class<? extends ResponseHandler> responseHandler() default ResponseHandlerDefaultImpl.class;
	public String responseFormat() default Format.UNSPECIFIED;
}
