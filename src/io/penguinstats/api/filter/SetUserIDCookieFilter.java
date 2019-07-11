package io.penguinstats.api.filter;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NewCookie;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@UserRelated
public class SetUserIDCookieFilter implements ContainerResponseFilter {

	private static Logger logger = LogManager.getLogger(SetUserIDCookieFilter.class);

	@Context
	private HttpServletRequest request;

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		HttpSession session = request.getSession();
		if (session != null && session.getAttribute("isNewUser") != null
				&& (boolean)session.getAttribute("isNewUser") == true && session.getAttribute("userID") != null) {
			String userID = (String)session.getAttribute("userID");
			responseContext.getHeaders().add("Set-Cookie",
					new NewCookie("userID", userID, "/", null, 1, null, Integer.MAX_VALUE, null, false, false));
			logger.info("Set Cookie for user " + userID);
			session.removeAttribute("isNewUser");
		}
	}

}
