package uk.co.praguematica.urlmapping.handlers;

import uk.co.praguematica.smservlet.UserContext;
import uk.co.praguematica.urlmapping.MappingProcessorError;
import uk.co.praguematica.urlmapping.MappingProcessorException;
import uk.co.praguematica.urlmapping.annotations.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SecurityHandler {
	boolean checkAuthentication(HttpServletRequest request);
	UserContext getUserContext(HttpServletRequest request);
}
