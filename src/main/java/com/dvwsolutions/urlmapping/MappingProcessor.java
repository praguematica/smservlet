package com.dvwsolutions.urlmapping;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dvwsolutions.urlmapping.annotations.Attribute;
import com.dvwsolutions.urlmapping.annotations.PathVariable;
import com.dvwsolutions.urlmapping.annotations.RequestMapping;
import com.dvwsolutions.urlmapping.annotations.SelfMapped;
import com.dvwsolutions.urlmapping.handlers.ResponseHandler;
import com.dvwsolutions.urlmapping.handlers.DefaultHandler;
import com.dvwsolutions.urlmapping.serializers.Format;
import com.dvwsolutions.urlmapping.serializers.Serializer;
import com.dvwsolutions.urlmapping.serializers.SerializerJsonImpl;
import com.dvwsolutions.urlmapping.serializers.SerializerNoneImpl;
import com.dvwsolutions.urlmapping.serializers.SerializerStringImpl;

public class MappingProcessor {	
	private SelfMapped selfMappedClassAnnotation;
	private List<Method> annotatedMethods;
	private Object annotatedObject;
	
	private Serializer defaultSerializer = new SerializerNoneImpl();
	private Map<String, Serializer> serializers = new HashMap<String, Serializer>();
	
	public MappingProcessor(Object annotatedObject) throws MappingProcessorException {
		this.annotatedObject = annotatedObject;
		selfMappedClassAnnotation = annotatedObject.getClass().getAnnotation(SelfMapped.class);
		this.annotatedMethods = AnnotationUtils.getAnnotatedMethods(RequestMapping.class, annotatedObject);
		
		// Register serializers
		registerSerializer(new SerializerStringImpl());
		registerSerializer(new SerializerJsonImpl());
		
	}
	
	public void registerSerializer(Serializer serializer) throws MappingProcessorException {
		if (serializer == null) return;
		if (serializer.type() == null) throw new MappingProcessorException(MappingProcessorExceptionType.SERIALIZE_INIT_ERROR, "Registered serializer does not have type set");
		serializers.put(serializer.type(), serializer);
	}
	
	/**
	 * Based on the path, it finds the method to be run and runs it
	 * @param path
	 * @param request
	 * @param response
	 * @return
	 * @throws MappingProcessorException 
	 * @throws Exception
	 */
	public void process(String path, HttpServletRequest request, HttpServletResponse response) throws MappingProcessorException {
		for (Method method : annotatedMethods) {
			RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
			Serializer serializer;
			if (!requestMapping.responseFormat().equals(Format.UNSPECIFIED)) 
				serializer = serializers.get(requestMapping.responseFormat());
			else if (selfMappedClassAnnotation != null && selfMappedClassAnnotation.responseFormat() != null)
				serializer = serializers.get(selfMappedClassAnnotation.responseFormat());
			else
				serializer = defaultSerializer;
			
			if (requestMapping != null) {
				try {
					Object methodResult = getMethodResult(path, method, requestMapping, request, response);
					if (methodResult != null) {
						if (serializer != null) {
							methodResult = serializer.serialize(methodResult);
						}
						processMethodResult(response, methodResult, requestMapping);
						return;
					}
				} catch (MappingProcessorException e) {
					throw e;						
				} catch (Throwable e) {
					String message = e.getMessage();
					MappingProcessorError err = new MappingProcessorError(message, e);
					Object errContents = message;
					if (serializer != null) {
						errContents = serializer.serialize(err);
					}
					processMethodFault(response, errContents, err, requestMapping);
					return;
				}
			}
		}
		// No suitable method was found, so handle the null result;
		throw new MappingProcessorException(MappingProcessorExceptionType.MAPPING_NOT_FOUND);
		//processMethodResult(response, null, null);
	}
	
	/**
	 * Internal success handler
	 * @param result
	 * @param method
	 * @param requestMapping
	 * @param response
	 * @throws MappingProcessorException 
	 * @throws Exception
	 */
	private void processMethodResult(HttpServletResponse response, Object result, RequestMapping requestMapping) throws MappingProcessorException {
		if (requestMapping == null) throw new MappingProcessorException(MappingProcessorExceptionType.OTHER, "RequestMapping annotation missing in processMethodResult");
		ResponseHandler responseHandler;
		try {
			if (requestMapping.responseHandler() != DefaultHandler.class) {
				responseHandler = requestMapping.responseHandler().newInstance();
			} else if (selfMappedClassAnnotation != null && selfMappedClassAnnotation.responseHandler() != null) {
				responseHandler = selfMappedClassAnnotation.responseHandler().newInstance();
			} else {
				responseHandler = DefaultHandler.class.newInstance();
			}
		} catch (Exception e) {
			throw new MappingProcessorException(MappingProcessorExceptionType.OTHER, "Unable to initialize response handler", e);
		}
		
		responseHandler.result(response, result, requestMapping);		
	}
	
	/**
	 * Internal failure handler
	 * @param message
	 * @param e
	 * @param response
	 * @throws MappingProcessorException 
	 * @throws Exception
	 */
	
	private void processMethodFault(HttpServletResponse response, Object errContents, MappingProcessorError err, RequestMapping requestMapping) throws MappingProcessorException {
		ResponseHandler responseHandler;
		try {
			if (requestMapping != null && requestMapping.responseHandler() != DefaultHandler.class) {
				responseHandler = requestMapping.responseHandler().newInstance();
			} else if (selfMappedClassAnnotation != null && selfMappedClassAnnotation.responseHandler() != null) {
				responseHandler = selfMappedClassAnnotation.responseHandler().newInstance();
			} else {
				responseHandler = DefaultHandler.class.newInstance();
			}
		} catch (Exception ee) {
			throw new MappingProcessorException(MappingProcessorExceptionType.OTHER, "Unable to initialize error handler", ee);
		}
		responseHandler.fault(response, errContents, err);
	}
	
	/**
	 * Invokes the method passing path, get and post parameters to appropriate variables.
	 * @param path
	 * @param method
	 * @param requestMapping
	 * @param request
	 * @return Object with results or null, if the method does not match the pattern
	 * @throws Throwable 
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 */
	private Object getMethodResult(String path, Method method, RequestMapping requestMapping, HttpServletRequest request, HttpServletResponse response) throws Throwable {
		if (path == null) throw new IllegalArgumentException("path is null");
		if (path.indexOf("{") > -1) throw new IllegalArgumentException("path contains restricted character '{'");
		if (path.indexOf("}") > -1) throw new IllegalArgumentException("path contains restricted character '}'");
		
		String ptn = requestMapping.value();
		if (path.equals(requestMapping)) {
			try {
				return method.invoke(this.annotatedObject);
			} catch (IllegalArgumentException e) {
				throw new MappingProcessorException(MappingProcessorExceptionType.ILLEGAL_ARGUMENTS, e);
			} catch (IllegalAccessException e) {
				throw new MappingProcessorException(MappingProcessorExceptionType.ILLEGAL_ACCESS, e);
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}			
		}
		

		Map<String, String> urlParamValues = getPathVariables(ptn, path);
		if (urlParamValues == null) { // if paramvalues null, then the method does not match the pattern
			return null;
		}
								
		Annotation[][] annotations = method.getParameterAnnotations();
		Class<?>[] paramTypes = method.getParameterTypes();	
		Object[] paramValues = new Object[paramTypes.length];

		for (int i = 0; i < paramTypes.length; i++) {
			if (ServletRequest.class.isAssignableFrom(paramTypes[i])) {
				paramValues[i] = request;
				continue;
			}
			
			if (ServletResponse.class.isAssignableFrom(paramTypes[i])) {
				paramValues[i] = response;
				continue;
			}
			
			if (HttpSession.class.isAssignableFrom(paramTypes[i])) {
				paramValues[i] = request!=null?request.getSession():null;
				continue;
			}
						
			Set<Class<?>> allowedTypes = new HashSet<Class<?>>();
			allowedTypes.add(String.class);
			allowedTypes.add(Integer.class);
			allowedTypes.add(Long.class);
			
//			if (!allowedTypes.contains(paramTypes[i])) {
//				throw new MappingProcessorException(MappingProcessorExceptionType.UNSUPPORTED_ATTRIBUTE_TYPE, "Unsupported attribute type: " + paramTypes[i].getCanonicalName());
//			}
			
			for (Annotation a: annotations[i]) {
				if (a instanceof PathVariable) {
					PathVariable pathVariable = (PathVariable) a;
					String urlParamValue = urlParamValues.get(pathVariable.value()); 
					try {
						// Url param comes back always as string, use corresponding deserializer to sort integer and long params
						Serializer stringSerializer = serializers.get(Format.STRING);
						if (stringSerializer != null) {
							paramValues[i] = stringSerializer.deserialize(URLDecoder.decode(urlParamValue, "UTF-8"), paramTypes[i]);
						} else {
							throw new MappingProcessorException(MappingProcessorExceptionType.DESERIALIZE_ERROR, "Can't find a serializer for " + Format.STRING + " used by PathVariable attribute");
						}

						// Retype if necessary
						if (Long.class.equals(paramTypes[i]) || long.class.equals(paramTypes[i])) {
							paramValues[i] = Long.parseLong(String.valueOf(paramValues[i]));
						}
						if (Integer.class.equals(paramTypes[i]) || int.class.equals(paramTypes[i])) {
							paramValues[i] = Integer.parseInt(String.valueOf(paramValues[i]));
						}

						break;
					} catch (UnsupportedEncodingException e) {
						throw new MappingProcessorException(MappingProcessorExceptionType.PATH_VARIABLE_URL_DECODE, e);
					} catch (NumberFormatException e) {
						throw new MappingProcessorException(MappingProcessorExceptionType.PATH_VARIABLE_FORMAT, "Cannot parse value for PathVariable " + pathVariable.value(), e);
					}
				}
				if (a instanceof Attribute) {
					Attribute atr = (Attribute) a;
					String atrFormat = atr.format();
					Serializer serializer = serializers.get(atrFormat);
					if (serializer == null) 
						throw new MappingProcessorException(MappingProcessorExceptionType.DESERIALIZE_ERROR, "No suitable serializer found for type: " + atr.format());
					
					try {
						paramValues[i] = serializer.deserialize(request.getParameter(atr.value()), paramTypes[i]);
					} catch (Exception e) {
						throw new MappingProcessorException(MappingProcessorExceptionType.DESERIALIZE_ERROR, e);
					}
					break;
				}					
			}			
		}
		
		try {
			return method.invoke(this.annotatedObject, paramValues);
		} catch (IllegalArgumentException e) {
			throw new MappingProcessorException(MappingProcessorExceptionType.ILLEGAL_ARGUMENTS, e);
		} catch (IllegalAccessException e) {
			throw new MappingProcessorException(MappingProcessorExceptionType.ILLEGAL_ACCESS, e);
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}		
	}	
	
	/**
	 * Processes url and get hashmap with list of properties with their values. Returns null if the method does not match
	 * @param urlPattern
	 * @param url
	 * @return
	 */
	private Map<String, String> getPathVariables(String urlPattern, String url) {
		String varPattern = "(\\{)([^/]+?)(\\})";
		Map<String, String> result = new HashMap<String, String>();

		if (urlPattern.equals(url)) return result;
		
		Matcher m = Pattern.compile(varPattern).matcher(urlPattern);
		int varCount = 0;
		while(m.find()) {
			String match = m.group(0);
			int startPosPattern = urlPattern.indexOf(match);
			String leftFromPattern = urlPattern.substring(0, startPosPattern);
			String rightFromPattern = urlPattern.substring(startPosPattern + match.length());
			String lrPattern = (leftFromPattern + "([^/]+)" + rightFromPattern).replaceAll(varPattern, "[^/]+");
			if (url.matches(lrPattern)) {
				varCount ++;
				String varVal = url.replaceAll(lrPattern, "$1");
				result.put(match.replaceAll("[\\{\\}]", ""), varVal);
			}
		}
		if (varCount == 0)
			return null;
 
		return result;
	}
}


