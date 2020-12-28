package io.penguinstats.controller.v2.api;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
@Api(tags = {"Account"})
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

	@ApiOperation(value = "Login with User ID", notes = "Login with the specified User ID.")
	@PostMapping(produces = "text/plain;charset=UTF-8")
	@ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully logged the user in"),
			@ApiResponse(code = 400, message = "User not found")})
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

	@ApiOperation(value = "Get authorization URL", notes = "Generate URL for third-party platform authorization.")
	@GetMapping(path = "/oauth/{platform}/authorize", produces = "text/plain;charset=UTF-8")
	@ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully generated auth URL."),
			@ApiResponse(code = 400, message = "Platform not found")})
	public ResponseEntity<String> authorize(
			@ApiParam(value = "Indicate which platfrom's auth URL should be generated.",
					required = true) @PathVariable("platform") String platformStr,
			@ApiParam(value = "Indicate the final redirect URI after OAuth is done.",
					required = false) @RequestParam(name = "redirect_uri", required = false) String redirectURI,
			HttpServletRequest request) {
		try {
			SocialPlatform platform = SocialPlatform.toSocialPlatform(platformStr);
			if (platform == null)
				return new ResponseEntity<>("Platform not found", HttpStatus.BAD_REQUEST);

			String userID = cookieUtil.readUserIDFromCookie(request);
			Authorization auth = authorizationService.getAuthorizationByUserIDAndPlatform(userID, platform);
			if (auth == null)
				auth = new Authorization();
			String state = AuthStateUtils.createState();
			auth.setState(state);
			auth.setRedirectURI(redirectURI);
			auth.setPlatform(platform);
			if (userID != null)
				auth.setUserID(userID);
			auth.setAuthRequestTime(System.currentTimeMillis());

			AuthRequest authRequest = factory.get(platformStr);
			String url = authRequest.authorize(state);

			authorizationService.saveAuthorization(auth);

			return new ResponseEntity<>(url, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error in authorize", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@ApiOperation(value = "Callback endpoint for social platforms",
			notes = "Use the code provided by third-party platforms to request for access token.")
	@RequestMapping(path = "/oauth/{platform}/callback", produces = "text/plain;charset=UTF-8")
	public ResponseEntity<String> callback(
			@ApiParam(value = "Indicate which platfrom this request is coming from.",
					required = true) @PathVariable("platform") String platformStr,
			@ApiParam(value = "Error message for QQ", required = false) @RequestParam(name = "msg",
					required = false) String msg,
			@ApiParam(value = "The auth callback from 3rd-party platform.", required = true) AuthCallback callback,
			HttpServletResponse response) {
		try {
			SocialPlatform platform = SocialPlatform.toSocialPlatform(platformStr);
			if (platform == null)
				return new ResponseEntity<>("Platform not found", HttpStatus.BAD_REQUEST);

			String state = callback.getState();
			Authorization auth = authorizationService.getAuthorizationByStateAndPlatform(state, platform);
			if (auth == null) {
				logger.warn("Failed to find state {} in platform {}.", state, platform);
				return new ResponseEntity<>("State is not correct", HttpStatus.BAD_REQUEST);
			}

			if (SocialPlatform.QQ.equals(platform) && msg != null) {
				logger.error("Error msg from QQ: {}, state = {}", msg, state);
				return new ResponseEntity<>(HttpStatus.OK);
			}

			AuthRequest authRequest = factory.get(platformStr);
			AuthResponse<?> res = authRequest.login(callback);
			System.out.println(res.getData().toString());

			String redirectURI = Optional.ofNullable(auth.getRedirectURI()).orElse("https://penguin-stats.cn/");
			response.sendRedirect(redirectURI);
			return new ResponseEntity<>(HttpStatus.TEMPORARY_REDIRECT);
		} catch (Exception e) {
			logger.error("Error in callback", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
