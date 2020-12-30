package io.penguinstats.service;

import java.util.List;

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
	public Authorization getAuthorizationByPlatformAndUuid(SocialPlatform platform, String uuid) {
		return authorizationDao.findByPlatformAndUuid(platform, uuid);
	}

	@Override
	public List<Authorization> getAuthorizationByUserId(String userID) {
		return authorizationDao.findByUserID(userID);
	}

	@Override
	public Authorization getAuthorizationByUserIdAndPlatform(String userID, SocialPlatform platform) {
		return authorizationDao.findByUserIDAndPlatform(userID, platform);
	}

	@Override
	public void deleteAuthorizationByUserIDAndPlatform(String userID, SocialPlatform platform){
		authorizationDao.deleteByUserIDAndPlatform(userID, platform);
	}
}
