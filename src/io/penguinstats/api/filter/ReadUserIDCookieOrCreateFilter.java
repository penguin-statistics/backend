package io.penguinstats.api.filter;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import io.penguinstats.api.filter.annotation.ReadUserIDCookieOrCreate;
import io.penguinstats.service.UserService;
import io.penguinstats.util.APIUtil;

@ReadUserIDCookieOrCreate
public class ReadUserIDCookieOrCreateFilter implements ContainerRequestFilter {

	private static final UserService userService = UserService.getInstance();

	@Context
	private HttpServletRequest request;
	@Context
	private HttpHeaders headers;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String userID = APIUtil.getUserIDFromCookies(request, headers);
		if (userID == null) {
			userID = userService.createNewUser(APIUtil.getClientIp(request));
		}
		APIUtil.setUserIDInSession(request, userID);
	}

}
