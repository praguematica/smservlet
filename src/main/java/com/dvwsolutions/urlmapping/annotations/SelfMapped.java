package com.dvwsolutions.urlmapping.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.dvwsolutions.urlmapping.handlers.ResponseHandler;
import com.dvwsolutions.urlmapping.handlers.DefaultHandler;
import com.dvwsolutions.urlmapping.serializers.Format;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SelfMapped {
	public Class<? extends ResponseHandler> responseHandler() default DefaultHandler.class;
	public String responseFormat() default Format.UNSPECIFIED;
}
