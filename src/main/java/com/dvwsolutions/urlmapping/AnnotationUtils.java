package com.dvwsolutions.urlmapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AnnotationUtils {
	public static List<Method> getAnnotatedMethods(Class<? extends Annotation> annotationClass, Object object) {
		List<Method> result = new ArrayList<Method>();
		Class<?> clazz = object.getClass();
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			Annotation urlPtn = method.getAnnotation(annotationClass);
			if (urlPtn != null) {
				result.add(method);
			}
		}
		return result;
	}
}
