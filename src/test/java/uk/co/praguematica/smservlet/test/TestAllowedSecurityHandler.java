package uk.co.praguematica.smservlet.test;

import uk.co.praguematica.smservlet.UserContext;
import uk.co.praguematica.urlmapping.handlers.SecurityHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by mfa on 07/10/2016.
 */
public class TestAllowedSecurityHandler implements SecurityHandler {
    public boolean checkAuthentication(HttpServletRequest request) {
        return true;
    }

    public UserContext getUserContext(HttpServletRequest request) {
        return new UserContext("test", "test user");
    }
}
