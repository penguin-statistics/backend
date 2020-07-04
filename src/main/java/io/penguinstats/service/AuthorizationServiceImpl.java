package io.penguinstats.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.penguinstats.dao.AuthorizationDao;
import io.penguinstats.enums.SocialPlatform;
import io.penguinstats.model.Authorization;

@Service("authorizationService")
public class AuthorizationServiceImpl implements AuthorizationService {

	@Autowired
	private AuthorizationDao authorizationDao;

	@Override
	public void saveAuthorization(Authorization authorization) {
		authorizationDao.save(authorization);
	}

	@Override
	public Authorization getAuthorizationByUserIDAndPlatform(String userID, SocialPlatform platform) {
		return authorizationDao.findByUserIDAndPlatform(userID, platform);
	}

	@Override
	public Authorization getAuthorizationByStateAndPlatform(String state, SocialPlatform platform) {
		return authorizationDao.findByStateAndPlatform(state, platform);
	}

}
