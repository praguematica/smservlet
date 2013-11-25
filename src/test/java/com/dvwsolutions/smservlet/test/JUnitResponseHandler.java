package com.dvwsolutions.smservlet.test;

import javax.servlet.http.HttpServletResponse;

import com.dvwsolutions.urlmapping.MappingProcessorError;
import com.dvwsolutions.urlmapping.MappingProcessorException;
import com.dvwsolutions.urlmapping.annotations.RequestMapping;
import com.dvwsolutions.urlmapping.handlers.ResponseHandler;

public class JUnitResponseHandler implements ResponseHandler {

	public static String responseResult;
	public static String responseFault;
	
	@Override
	public void result(HttpServletResponse response, Object result, RequestMapping requestMapping) throws MappingProcessorException {
		responseResult = String.valueOf(result);
		
	}
	
	@Override
	public void fault(HttpServletResponse response, Object errContents, MappingProcessorError err) {
		responseFault = String.valueOf(errContents);			
	}
}
