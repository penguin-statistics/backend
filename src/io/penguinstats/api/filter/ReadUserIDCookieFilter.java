package io.penguinstats.api.filter;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.penguinstats.service.UserService;
import io.penguinstats.util.APIUtil;

@UserRelated
public class ReadUserIDCookieFilter implements ContainerRequestFilter {

	private static final UserService userService = UserService.getInstance();

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
				if (userID != null) {
					if (userService.getUser(userID) == null) {
						logger.warn("userID " + userID + "is not existed.");
						userID = null;
						isNewUser = true;
					} else {
						// old user
						isNewUser = false;
						String ip = APIUtil.getClientIp(request);
						if (ip != null)
							userService.addIP(userID, ip);
					}
				} else {
					// userID == null
					logger.warn("userID's value in the cookie map is null.");
					isNewUser = true;
				}
			} else {
				// cookie == null, userID is not existed in the cookie
				isNewUser = true;
			}
		} else {
			// map == null
			logger.error("Failed to get cookies from header.");
			isNewUser = true;
		}

		if (userID == null) {
			userID = userService.createNewUser(APIUtil.getClientIp(request));
			isNewUser = true;
		}

		if (userID != null) {
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

}
