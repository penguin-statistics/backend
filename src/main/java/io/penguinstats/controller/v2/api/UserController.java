package io.penguinstats.controller.v2.api;

import java.util.List;
import java.net.URL;
import java.net.MalformedURLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.xkcoding.justauth.AuthRequestFactory;

import io.penguinstats.enums.SocialPlatform;
import io.penguinstats.model.Authorization;
import io.penguinstats.model.User;
import io.penguinstats.service.AuthorizationService;
import io.penguinstats.service.UserService;
import io.penguinstats.util.CookieUtil;
import io.penguinstats.util.IpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;

@RestController("userController_v2")
@RequestMapping("/api/v2/users")
@Api(tags = { "Account" })
public class UserController {

	public static final String INTERNAL_USER_ID_PREFIX = "internal_";

	private static Logger logger = LogManager.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private AuthorizationService authorizationService;

	@Autowired
	private AuthRequestFactory factory;

	@Autowired
	private CookieUtil cookieUtil;

	@Autowired
	private CacheManager cacheManager;

	@ApiOperation(value = "Login with User ID", notes = "Login with the specified User ID.")
	@PostMapping(produces = "text/plain;charset=UTF-8")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully logged the user in"),
			@ApiResponse(code = 400, message = "User not found") })
	public ResponseEntity<String> login(@RequestBody String userID, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		boolean isInternal = false;
		if (userID.startsWith(INTERNAL_USER_ID_PREFIX)) {
			isInternal = true;
			userID = userID.substring(INTERNAL_USER_ID_PREFIX.length());
		}
		User user = userService.getUserByUserID(userID);
		if (user == null) {
			if (isInternal) {
				userID = userService.createNewUser(userID, IpUtil.getIpAddr(request));
				if (userID != null) {
					userService.addTag(userID, "internal");
				}
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		}
		CookieUtil.setUserIDCookie(response, userID);
		return new ResponseEntity<>(new JSONObject().put("userID", userID).toString(), HttpStatus.OK);
	}

	@ApiOperation(value = "oauth2 info", notes = "Oauth2 platform status related to user")
	@GetMapping(path = "/oauth2", produces = "text/plain;charset=UTF-8")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "User oauth2 info."),
			@ApiResponse(code = 404, message = "User not found") })
	public ResponseEntity<String> oauth2State(HttpServletRequest request, HttpServletResponse response) {
		String userID = cookieUtil.readUserIDFromCookie(request);
		User user = userService.getUserByUserID(userID);
		if (user != null) {
			List<Authorization> authorizationList = authorizationService.getAuthorizationByUserId(userID);

			// dump authorizationList to json response
			JSONObject jObject = new JSONObject();
			for (Authorization authorization : authorizationList) {
				JSONObject authorizationJson = new JSONObject();
				authorizationJson.put("platform", authorization.getPlatform());
				authorizationJson.put("username", authorization.getUsername());
				jObject.put(authorization.getPlatform().toString(), authorizationJson);
			}

			return new ResponseEntity<>(jObject.toString(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@ApiOperation(value = "Login with User ID and link oauth2", notes = "Login with the specified User ID and link with social platform.")
	@PostMapping(path = "/oauth2", produces = "text/plain;charset=UTF-8")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully logged the user in"),
			@ApiResponse(code = 400, message = "User not found") })
	public ResponseEntity<String> oauth2Login(@RequestBody(required = false) String userID, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		// login or create userID
		if (userID != null) {
			User user = userService.getUserByUserID(userID);
			if (user == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} else {
			userID = userService.createNewUser(IpUtil.getIpAddr(request));
		}

		// retrieve oauth2 info
		String state = cookieUtil.readOauth2StateFromCookie(request);
		Cache cache = cacheManager.getCache("oauth2");
		String resString = cache.get(state + "_userData", String.class);
		JSONObject res = new JSONObject(resString);
		String platformStr = cache.get(state + "_platform", String.class);
		SocialPlatform platform = SocialPlatform.toSocialPlatform(platformStr);

		// Save authorization
		Authorization newAuthorization = new Authorization();
		newAuthorization.setUserID(userID);
		newAuthorization.setPlatform(platform);
		newAuthorization.setUsername(res.getJSONObject("data").getString("username"));
		newAuthorization.setUuid(res.getJSONObject("data").getString("uuid"));
		newAuthorization.setAccessToken(res.getJSONObject("data").getJSONObject("token").getString("accessToken"));
		newAuthorization
				.setRefreshToken(res.getJSONObject("data").getJSONObject("token").optString("refreshToken", null));
		newAuthorization.setExpireAt(System.currentTimeMillis()
				+ res.getJSONObject("data").getJSONObject("token").getLong("expireIn") * 1000);
		authorizationService.saveAuthorization(newAuthorization);

		cache.evict(state + "_userData");
		cache.evict(state + "_platform");
		
		// login user with cookie
		CookieUtil.setUserIDCookie(response, userID);
		return new ResponseEntity<>(new JSONObject().put("userID", userID).toString(), HttpStatus.OK);
	}

	@ApiOperation(value = "Unlink social account", notes = "Unlink soical platform with user")
	@DeleteMapping(path = "/oauth2", produces = "text/plain;charset=UTF-8")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "User oauth2 info."),
			@ApiResponse(code = 404, message = "User not found") })
	public ResponseEntity<String> deleteOauth2(@RequestBody String platformStr, HttpServletRequest request,
			HttpServletResponse response) {
		SocialPlatform platform = SocialPlatform.toSocialPlatform(platformStr);
		String userID = cookieUtil.readUserIDFromCookie(request);
		User user = userService.getUserByUserID(userID);
		if (user != null) {
			Authorization authorization = authorizationService.getAuthorizationByUserIdAndPlatform(userID, platform);
			if (authorization != null) {
				authorizationService.deleteAuthorizationByUserIDAndPlatform(userID, platform);
				return new ResponseEntity<>("Social platform updated", HttpStatus.OK);
			} else {
				return new ResponseEntity<>("Social platform link not found", HttpStatus.NOT_FOUND);
			}
		} else {
			return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
		}
	}

	@ApiOperation(value = "Get authorization URL", notes = "Generate URL for third-party platform authorization.")
	@GetMapping(path = "/oauth/{platform}/authorize", produces = "text/plain;charset=UTF-8")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully generated auth URL."),
			@ApiResponse(code = 400, message = "Platform not found"),
			@ApiResponse(code = 400, message = "Invalid redirect_uri") })
	public ResponseEntity<String> authorize(
			@ApiParam(value = "Indicate which platfrom's auth URL should be generated.", required = true) @PathVariable("platform") String platformStr,
			@ApiParam(value = "Indicate the final redirect URI after OAuth is done.", required = false) @RequestParam(name = "redirect_uri", required = false) String redirectURI,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			// Is SocialPlatform supported?
			SocialPlatform platform = SocialPlatform.toSocialPlatform(platformStr);
			if (platform == null)
				return new ResponseEntity<>("Platform not found", HttpStatus.BAD_REQUEST);

			// Is redirectURI validated?
			if (redirectURI != null) {
				try {
					String redirectURIHost = (new URL(redirectURI)).getHost();
					System.out.println(redirectURIHost);
					if (redirectURIHost == "localhost" || redirectURIHost == "penguin-stats.io"
							|| redirectURIHost == "penguin-stats.io") {
						// FIXME Prefer using a function to check if the redirectURI is validated
						return new ResponseEntity<>("Invalid redirect_uri", HttpStatus.BAD_REQUEST);
					}
				} catch (MalformedURLException e) {
					return new ResponseEntity<>("Invalid redirect_uri", HttpStatus.BAD_REQUEST);
				}
			} else {
				redirectURI = "http://penguin-stats.io/"; // TODO CN/Global main page
			}

			// Generate authorzie url of third party platform
			AuthRequest authRequest = factory.get(platformStr);
			String state = AuthStateUtils.createState();

			// TODO: authorize url should be different for CN mirror / Global mirror
			String authorizeUrl = authRequest.authorize(state);

			// Store oauth2 in cache for redirecting to frontend
			Cache cache = cacheManager.getCache("oauth2");
			cache.put(state + "_redirectURI", redirectURI);

			// Redirect to oauth2 server
			response.sendRedirect(authorizeUrl);
			return new ResponseEntity<>(HttpStatus.TEMPORARY_REDIRECT);
		} catch (Exception e) {
			logger.error("Error in authorize", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@ApiOperation(value = "Callback endpoint for social platforms", notes = "Use the code provided by third-party platforms to request for access token.")
	@GetMapping(path = "/oauth/{platform}/callback", produces = "text/plain;charset=UTF-8")
	@ApiResponses(value = {
			@ApiResponse(code = 302, message = "Successfully loggedin, redirecting to redirect_uri with penguin_token"),
			@ApiResponse(code = 400, message = "Invalidated state"),
			@ApiResponse(code = 500, message = "There is error message") })
	public ResponseEntity<String> callback(
			@ApiParam(value = "Indicate which platfrom this request is coming from.", required = true) @PathVariable("platform") String platformStr,
			@ApiParam(value = "Error message", required = false) @RequestParam(name = "msg", required = false) String msg,
			@ApiParam(value = "The auth callback from 3rd-party platform.", required = true) AuthCallback callback,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			// Is SocialPlatform supported?
			SocialPlatform platform = SocialPlatform.toSocialPlatform(platformStr);
			if (platform == null)
				return new ResponseEntity<>("Platform not found", HttpStatus.BAD_REQUEST);

			// Is state validated?
			// JustAuth will check state with platform
			String state = callback.getState();

			// Oauth with third party to get user info
			AuthRequest authRequest = factory.get(platformStr);
			AuthResponse<?> authResponse = authRequest.login(callback);
			JSONObject res = new JSONObject(authResponse);
			String oauth2_uuid = res.getJSONObject("data").getString("uuid");

			String userID = null;
			User user = null;

			// get User
			Authorization loginAuthorization = authorizationService.getAuthorizationByPlatformAndUuid(platform,
					oauth2_uuid);
			if (loginAuthorization != null) {
				userID = loginAuthorization.getUserID();
				if (userID != null) {
					user = userService.getUserByUserID(userID);
				}
			}

			// Get cached redirectURI for this oauth
			Cache cache = cacheManager.getCache("oauth2");
			String redirectURIString = cache.get(state + "_redirectURI", String.class);
			if (redirectURIString == null) {
				// TODO CN/Global mirror
				redirectURIString = "http://penguin-stats.io/";
			}
			URL redirectURI = new URL(redirectURIString);

			if (user != null) { 
				// Oauth2 is linked with a penguin id, login in user
				String cookieUserID = cookieUtil.readUserIDFromCookie(request);
				if (cookieUserID != null && cookieUserID != userID) {
					logger.error("Cannot link to this penguin id, as it has linked to another penguin id.");
					return new ResponseEntity<>(
							"Cannot link to this penguin id, as it has linked to another penguin id.",
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
				// Set authorize credientials
				CookieUtil.setUserIDCookie(response, userID);

				String redirectURIAuth = UriComponentsBuilder.fromUriString(redirectURI.toString())
						.replaceQueryParam("auth", "loggedIn").build().toUriString(); // for frontend

				// remove cache
				cache.evict(state + "_redirectURI");

				response.sendRedirect(redirectURIAuth);
				return new ResponseEntity<>(HttpStatus.TEMPORARY_REDIRECT);
			} else {
				// User is logged in and linking a penguin id
				userID = cookieUtil.readUserIDFromCookie(request);
				User loggedInUser = null;
				if (userID != null) {
					loggedInUser = userService.getUserByUserID(userID);
				}
				if (loggedInUser != null) {
					// Check if the user want to bind two social accounts to one penguin id
					Authorization prevAuthorization = authorizationService.getAuthorizationByUserIdAndPlatform(userID,
							platform);
					if (prevAuthorization != null) {
						logger.error(
								"Error in trying to bind two or more social accounts of one platform in one penguin id");
						return new ResponseEntity<>(
								"Error in trying to bind two or more social accounts of one platform in one penguin id",
								HttpStatus.INTERNAL_SERVER_ERROR);
					}

					// Save authorization
					Authorization newAuthorization = new Authorization();
					newAuthorization.setUserID(userID);
					newAuthorization.setPlatform(platform);
					newAuthorization.setUsername(res.getJSONObject("data").getString("username"));
					newAuthorization.setUuid(oauth2_uuid);
					newAuthorization
							.setAccessToken(res.getJSONObject("data").getJSONObject("token").getString("accessToken"));
					newAuthorization.setRefreshToken(
							res.getJSONObject("data").getJSONObject("token").optString("refreshToken", null));
					newAuthorization.setExpireAt(System.currentTimeMillis()
							+ res.getJSONObject("data").getJSONObject("token").getLong("expireIn") * 1000);
					authorizationService.saveAuthorization(newAuthorization);

					// remove cache
					cache.evict(state + "_redirectURI");

					// redirect to frontend
					String redirectURIAuth = UriComponentsBuilder.fromUriString(redirectURI.toString())
							.replaceQueryParam("auth", "loggedIn").build().toUriString();
					response.sendRedirect(redirectURIAuth);
					return new ResponseEntity<>(HttpStatus.TEMPORARY_REDIRECT);
				} else {
					String redirectURIAuth = UriComponentsBuilder.fromUriString(redirectURI.toString())
							.replaceQueryParam("auth", "loginOrRegister").replaceQueryParam("state", state).build()
							.toUriString();

					cache.evict(state + "_redirectURI");
					cache.put(state + "_userData", res.toString());
					cache.put(state + "_platform", platform.toString());

					CookieUtil.setOauth2State(response, state);
					response.sendRedirect(redirectURIAuth);
					return new ResponseEntity<>(HttpStatus.TEMPORARY_REDIRECT);
				}
			}
		} catch (JSONException e) {
			logger.error("Error in oauth2 auth (getting user profile)", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (me.zhyd.oauth.exception.AuthException e) {
			logger.error("Error in oauth2 auth", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
