package uk.co.praguematica.urlmapping.handlers;

import uk.co.praguematica.smservlet.UserContext;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by mfa on 07/10/2016.
 */
public class DefaultSecurityHandler implements SecurityHandler {
    public boolean checkAuthentication(HttpServletRequest request) {
        return true;
    }

    public UserContext getUserContext(HttpServletRequest request) {
        UserContext userContext = new UserContext("anonymous", "Anonymous User");
        return userContext;
    }
}
