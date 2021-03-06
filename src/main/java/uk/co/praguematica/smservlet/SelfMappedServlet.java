package uk.co.praguematica.smservlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.co.praguematica.urlmapping.MappingProcessor;
import uk.co.praguematica.urlmapping.MappingProcessorException;

/**
 * Servlet implementation class UploadLogoServlet
 */
public abstract class SelfMappedServlet extends HttpServlet {

	private static final long serialVersionUID = 3172984602859820926L;
	
	protected MappingProcessor mappingProcessor;

	@Override
	public void init() throws ServletException {
		super.init();
		try {
			mappingProcessor = new MappingProcessor(this);
		} catch (Exception e) {
			throw new ServletException("Error when initializing servlet: " + e.getMessage(), e);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {				
		doProcess(request, response);		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doProcess(request, response);
	}
	
	protected void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String requestUri = request.getRequestURI();
		String path = requestUri.substring(getServletConfig().getServletContext().getContextPath().length()+1);
		
		try {
			mappingProcessor.process(path, request, response);
		} catch (MappingProcessorException e) {
			switch(e.getType()) {
			case NOT_FOUND: 
				response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
				break;
				
			case MAPPING_NOT_FOUND:
			case PATH_VARIABLE_FORMAT:
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
				break;
				
			case UNAUTHORIZED:
				response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
				break;
				
			default:
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
				break;
			}
		}
	}

	protected boolean checkAuthentication(HttpServletRequest request) {
		return true;
	}
}

