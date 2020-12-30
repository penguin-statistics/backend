package io.penguinstats.service;

import java.util.List;

import io.penguinstats.enums.SocialPlatform;
import io.penguinstats.model.Authorization;

public interface AuthorizationService {

	public void saveAuthorization(Authorization authorization);

	public Authorization getAuthorizationByPlatformAndUuid(SocialPlatform platform, String uuid);

	public Authorization getAuthorizationByUserIdAndPlatform(String userID, SocialPlatform platform);

	public List<Authorization> getAuthorizationByUserId(String userID);

	public void deleteAuthorizationByUserIDAndPlatform(String userID, SocialPlatform platform);

}
