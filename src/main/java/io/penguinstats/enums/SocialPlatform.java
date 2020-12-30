package io.penguinstats.enums;

public enum SocialPlatform {

	QQ, Google;

	public static SocialPlatform toSocialPlatform(String str) {
		if ("qq".equalsIgnoreCase(str))
			return QQ;
		if ("google".equalsIgnoreCase(str))
			return Google;
		else
			return null;
	}

}
