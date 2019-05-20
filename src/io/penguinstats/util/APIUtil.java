package io.penguinstats.util;

public class APIUtil {

	public static String convertStreamToString(java.io.InputStream is) {
		try (java.util.Scanner s = new java.util.Scanner(is)) {
			s.useDelimiter("\\A");
			String result = s.hasNext() ? s.next() : "";
			return result;
		}
	}

}
