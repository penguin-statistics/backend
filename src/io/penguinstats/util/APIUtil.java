package io.penguinstats.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class APIUtil {

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

}
