package co.uk.praguematica.urlmapping.handlers;

import javax.servlet.http.HttpServletResponse;

import co.uk.praguematica.urlmapping.MappingProcessorError;
import co.uk.praguematica.urlmapping.MappingProcessorException;
import co.uk.praguematica.urlmapping.annotations.RequestMapping;

public interface ResponseHandler {
	public void fault(HttpServletResponse response, Object errContents, MappingProcessorError err, RequestMapping requestMapping);	
	public void result(HttpServletResponse response, Object result, RequestMapping requestMapping) throws MappingProcessorException;
}
