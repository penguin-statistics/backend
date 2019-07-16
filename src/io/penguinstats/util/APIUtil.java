package io.penguinstats.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.penguinstats.service.UserService;

public class APIUtil {

	private static final UserService userService = UserService.getInstance();

	private static Logger logger = LogManager.getLogger(APIUtil.class);

	public static String convertStreamToString(java.io.InputStream is) {
		try (java.util.Scanner s = new java.util.Scanner(is)) {
			s.useDelimiter("\\A");
			String result = s.hasNext() ? s.next() : "";
			return result;
		}
	}

	public static String getClientIp(HttpServletRequest request) {
		String remoteAddr = null;
		try {
			if (request != null) {
				remoteAddr = request.getHeader("X-FORWARDED-FOR");
				if (remoteAddr == null || "".equals(remoteAddr)) {
					remoteAddr = request.getRemoteAddr();
				}
			}
		} catch (Exception e) {
			logger.error("Error in getClientIp", e);
		}
		return remoteAddr;
	}

	public static String getUserIDFromSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null)
			return null;
		return (String)session.getAttribute("userID");
	}

	public static void setUserIDInSession(HttpServletRequest request, String userID) {
		HttpSession session = request.getSession();
		if (session != null) {
			if (userID != null)
				session.setAttribute("userID", userID);
			else
				session.removeAttribute("userID");
		} else {
			logger.error("Failed to get session.");
		}
	}

	public static String getUserIDFromCookies(HttpServletRequest request, HttpHeaders headers) {
		String userID = null;
		Map<String, Cookie> map = headers.getCookies();
		if (map != null) {
			Cookie cookie = map.get("userID");
			if (cookie != null) {
				userID = cookie.getValue();
				if (userID != null) {
					try {
						userID = URLDecoder.decode(userID, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						logger.error("Error in getUserIDFromCookies: ", e);
						userID = null;
					}
				}
				if (userID != null) {
					if (userService.getUser(userID) == null) {
						logger.warn("userID " + userID + " is not existed.");
						userID = null;
					} else {
						// old user
						String ip = APIUtil.getClientIp(request);
						if (ip != null)
							userService.addIP(userID, ip);
					}
				} else {
					// userID == null
					logger.warn("userID's value in the cookie map is null.");
				}
			} else {
				// cookie == null, userID is not existed in the cookie
			}
		} else {
			// map == null
			logger.error("Failed to get cookies from header.");
		}
		return userID;
	}

}
