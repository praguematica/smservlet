package uk.co.praguematica.urlmapping.handlers;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import uk.co.praguematica.urlmapping.MappingProcessorError;
import uk.co.praguematica.urlmapping.MappingProcessorException;
import uk.co.praguematica.urlmapping.annotations.RequestMapping;


public class DefaultHandler implements ResponseHandler {

	public void fault(HttpServletResponse response, Object errContents, MappingProcessorError err, RequestMapping requestMapping) {
		try {
			response.setCharacterEncoding(requestMapping.encoding());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(errContents);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void result(HttpServletResponse response, Object result, RequestMapping requestMapping) throws MappingProcessorException {
		try {
			response.setCharacterEncoding(requestMapping.encoding());
			response.setContentType(requestMapping.contentType());
			response.getWriter().print(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
