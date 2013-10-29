package com.dvwsolutions.urlmapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dvwsolutions.urlmapping.annotations.Attribute;
import com.dvwsolutions.urlmapping.annotations.PathVariable;
import com.dvwsolutions.urlmapping.annotations.RequestMapping;
import com.dvwsolutions.urlmapping.annotations.SelfMapped;
import com.dvwsolutions.urlmapping.handlers.ResponseHandler;
import com.dvwsolutions.urlmapping.handlers.ResponseHandlerDefaultImpl;
import com.dvwsolutions.urlmapping.serializers.Format;
import com.dvwsolutions.urlmapping.serializers.Serializer;
import com.dvwsolutions.urlmapping.serializers.SerializerJsonImpl;
import com.dvwsolutions.urlmapping.serializers.SerializerStringImpl;

public class MappingProcessor {	
	private SelfMapped selfMappedClassAnnotation;
	private List<Method> annotatedMethods;
	private Object annotatedObject;
	
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
			RequestMapping urlPtn = method.getAnnotation(RequestMapping.class);
			Serializer serializer;
			if (!urlPtn.responseFormat().equals(Format.UNSPECIFIED)) 
				serializer = serializers.get(urlPtn.responseFormat());
			else
				serializer = serializers.get(selfMappedClassAnnotation.responseFormat());
			
			if (urlPtn != null) {
				try {
					Object methodResult = getMethodResult(path, method, urlPtn, request);
					if (methodResult != null) {
						if (serializer != null) {
							methodResult = serializer.serialize(methodResult);
						}
						processMethodResult(response, methodResult, urlPtn);
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
					processMethodFault(response, errContents, err, urlPtn);
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
	 * @param urlPtn
	 * @param response
	 * @throws MappingProcessorException 
	 * @throws Exception
	 */
	private void processMethodResult(HttpServletResponse response, Object result, RequestMapping urlPtn) throws MappingProcessorException {
		ResponseHandler responseHandler;
		try {
			if (urlPtn != null && urlPtn.responseHandler() != ResponseHandlerDefaultImpl.class) {
				responseHandler = urlPtn.responseHandler().newInstance();
			} else if (selfMappedClassAnnotation != null && selfMappedClassAnnotation.responseHandler() != null) {
				responseHandler = selfMappedClassAnnotation.responseHandler().newInstance();
			} else {
				responseHandler = ResponseHandlerDefaultImpl.class.newInstance();
			}
		} catch (Exception e) {
			throw new MappingProcessorException(MappingProcessorExceptionType.OTHER, "Unable to initialize response handler", e);
		}
		
		responseHandler.result(response, result);		
	}
	
	/**
	 * Internal failure handler
	 * @param message
	 * @param e
	 * @param response
	 * @throws MappingProcessorException 
	 * @throws Exception
	 */
	
	private void processMethodFault(HttpServletResponse response, Object errContents, MappingProcessorError err, RequestMapping urlPtn) throws MappingProcessorException {
		ResponseHandler responseHandler;
		try {
			if (urlPtn != null && urlPtn.responseHandler() != ResponseHandlerDefaultImpl.class) {
				responseHandler = urlPtn.responseHandler().newInstance();
			} else if (selfMappedClassAnnotation != null && selfMappedClassAnnotation.responseHandler() != null) {
				responseHandler = selfMappedClassAnnotation.responseHandler().newInstance();
			} else {
				responseHandler = ResponseHandlerDefaultImpl.class.newInstance();
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
	 * @param urlPtn
	 * @param request
	 * @return Object with results or null, if the method does not match the pattern
	 * @throws Throwable 
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 */
	private Object getMethodResult(String path, Method method, RequestMapping urlPtn, HttpServletRequest request) throws Throwable {
		if (path == null) throw new IllegalArgumentException("path is null");
		if (path.indexOf("{") > -1) throw new IllegalArgumentException("path contains restricted character '{'");
		if (path.indexOf("}") > -1) throw new IllegalArgumentException("path contains restricted character '}'");
		
		String ptn = urlPtn.value();
		if (path.equals(urlPtn)) {
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
			if (paramTypes[i] == ServletRequest.class) {
				paramValues[i] = request;
				continue;
			}
			
			if (paramTypes[i] == HttpSession.class) {
				paramValues[i] = request!=null?request.getSession():null;
				continue;
			}
			
//			if (paramTypes[i] == String.class) {
			for (Annotation a: annotations[i]) {
				if (a instanceof PathVariable) {
					paramValues[i] = urlParamValues.get(((PathVariable) a).value()); 
					break;
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
//			}
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


