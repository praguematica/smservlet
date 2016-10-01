package co.uk.praguematica.smservlet.test;

import javax.servlet.http.HttpServletResponse;

import co.uk.praguematica.urlmapping.MappingProcessorError;
import co.uk.praguematica.urlmapping.MappingProcessorException;
import co.uk.praguematica.urlmapping.annotations.RequestMapping;
import co.uk.praguematica.urlmapping.handlers.ResponseHandler;

public class JUnitResponseHandler implements ResponseHandler {

	public static String responseResult;
	public static String responseFault;
	
	@Override
	public void result(HttpServletResponse response, Object result, RequestMapping requestMapping) throws MappingProcessorException {
		responseResult = String.valueOf(result);
		
	}
	
	@Override
	public void fault(HttpServletResponse response, Object errContents, MappingProcessorError err, RequestMapping requestMapping) {
		responseFault = String.valueOf(errContents);			
	}
}
