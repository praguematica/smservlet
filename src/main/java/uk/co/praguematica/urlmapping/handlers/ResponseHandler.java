package uk.co.praguematica.urlmapping.handlers;

import javax.servlet.http.HttpServletResponse;

import uk.co.praguematica.urlmapping.MappingProcessorError;
import uk.co.praguematica.urlmapping.MappingProcessorException;
import uk.co.praguematica.urlmapping.annotations.RequestMapping;

public interface ResponseHandler {
	public void fault(HttpServletResponse response, Object errContents, MappingProcessorError err, RequestMapping requestMapping);	
	public void result(HttpServletResponse response, Object result, RequestMapping requestMapping) throws MappingProcessorException;
}
