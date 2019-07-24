package io.penguinstats.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.penguinstats.model.User;
import io.penguinstats.service.UserService;

@Component("cookieUtil")
public class CookieUtil {

	private static Logger logger = LogManager.getLogger(CookieUtil.class);
	private static CookieUtil cookieUtil;

	@Autowired
	private UserService userService;

	@PostConstruct
	public void init() {
		cookieUtil = this;
		cookieUtil.userService = this.userService;
	}

	public static void setUserIDCookie(HttpServletResponse response, String userID)
			throws UnsupportedEncodingException {
		Cookie cookie = new Cookie("userID", URLEncoder.encode(userID, "UTF-8"));
		cookie.setPath("/");
		cookie.setMaxAge(60 * 24 * 365);
		response.addCookie(cookie);
	}

	/** 
	 * @Title: readUserIDFromCookie 
	 * @Description: Read userID from cookies.
	 * @param request
	 * @return String
	 */
	public String readUserIDFromCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		String userID = null;
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("userID")) {
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
						User user = userService.getUserByUserID(userID);
						if (user == null) {
							logger.warn("userID " + userID + " is not existed.");
							userID = null;
						} else {
							// old user
							String ip = IpUtil.getIpAddr(request);
							if (ip != null && !user.containsIp(ip)) {
								logger.info("Add ip " + ip + " to user " + userID);
								userService.addIP(userID, ip);
							}
						}
					} else {
						// userID == null
						logger.warn("userID's value in the cookie map is null.");
					}
					break;
				}
			}
		}
		return userID;
	}

}
