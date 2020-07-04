package io.penguinstats.service;

import io.penguinstats.enums.SocialPlatform;
import io.penguinstats.model.Authorization;

public interface AuthorizationService {

	public void saveAuthorization(Authorization authorization);

	public Authorization getAuthorizationByUserIDAndPlatform(String userID, SocialPlatform platform);

	public Authorization getAuthorizationByStateAndPlatform(String state, SocialPlatform platform);

}
