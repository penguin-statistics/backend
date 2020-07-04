package io.penguinstats.enums;

public enum SocialPlatform {

	QQ;

	public static SocialPlatform toSocialPlatform(String str) {
		if ("qq".equalsIgnoreCase(str))
			return QQ;
		else
			return null;
	}

}
