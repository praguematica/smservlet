package com.dvwsolutions.urlmapping.handlers;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.dvwsolutions.urlmapping.MappingProcessorError;
import com.dvwsolutions.urlmapping.MappingProcessorException;
import com.dvwsolutions.urlmapping.annotations.RequestMapping;


public class DefaultHandler implements ResponseHandler {

	public void fault(HttpServletResponse response, Object errContents, MappingProcessorError err) {
		try {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(errContents);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void result(HttpServletResponse response, Object result, RequestMapping requestMapping) throws MappingProcessorException {
		try {
			response.setContentType(requestMapping.contentType());
			response.getWriter().print(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
