package uk.co.praguematica.smservlet.test;

import javax.servlet.http.HttpServletResponse;

import uk.co.praguematica.urlmapping.MappingProcessorError;
import uk.co.praguematica.urlmapping.MappingProcessorException;
import uk.co.praguematica.urlmapping.annotations.RequestMapping;
import uk.co.praguematica.urlmapping.handlers.ResponseHandler;

public class JUnitResponseHandler implements ResponseHandler {

	public static String responseResult;
	public static String responseFault;
	
	public void result(HttpServletResponse response, Object result, RequestMapping requestMapping) throws MappingProcessorException {
		responseResult = String.valueOf(result);
		
	}
	
	public void fault(HttpServletResponse response, Object errContents, MappingProcessorError err, RequestMapping requestMapping) {
		responseFault = String.valueOf(errContents);			
	}
}
