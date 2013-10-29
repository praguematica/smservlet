package com.dvwsolutions.urlmapping.handlers;

import javax.servlet.http.HttpServletResponse;

import com.dvwsolutions.urlmapping.MappingProcessorError;

public interface ResponseHandler {
	public void fault(HttpServletResponse response, Object errContents, MappingProcessorError err);	
	public void result(HttpServletResponse response, Object result);
}
