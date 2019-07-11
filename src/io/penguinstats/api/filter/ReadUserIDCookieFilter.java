package io.penguinstats.api.filter;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@UserRelated
public class ReadUserIDCookieFilter implements ContainerRequestFilter {

	private static Logger logger = LogManager.getLogger(ReadUserIDCookieFilter.class);

	@Context
	private HttpServletRequest request;
	@Context
	private HttpHeaders headers;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String userID = null;
		boolean isNewUser = true;

		Map<String, Cookie> map = headers.getCookies();
		if (map != null) {
			Cookie cookie = map.get("userID");
			if (cookie != null) {
				userID = cookie.getValue();
				isNewUser = false;
				logger.info("existing user " + userID);
			}
		} else {
			logger.error("Failed to get cookies from header.");
		}

		if (userID == null) {
			// TODO: create new user
			userID = Integer.toString(new Random().nextInt(1000));
			isNewUser = true;
			logger.info("create new user " + userID);
		}

		HttpSession session = request.getSession();
		if (session != null) {
			session.setAttribute("userID", userID);
			if (isNewUser)
				session.setAttribute("isNewUser", true);
		} else {
			logger.error("Failed to get session.");
		}
	}

}
