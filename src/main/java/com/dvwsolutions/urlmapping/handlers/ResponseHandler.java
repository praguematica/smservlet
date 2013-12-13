package com.dvwsolutions.urlmapping.handlers;

import javax.servlet.http.HttpServletResponse;

import com.dvwsolutions.urlmapping.MappingProcessorError;
import com.dvwsolutions.urlmapping.MappingProcessorException;
import com.dvwsolutions.urlmapping.annotations.RequestMapping;

public interface ResponseHandler {
	public void fault(HttpServletResponse response, Object errContents, MappingProcessorError err, RequestMapping requestMapping);	
	public void result(HttpServletResponse response, Object result, RequestMapping requestMapping) throws MappingProcessorException;
}
