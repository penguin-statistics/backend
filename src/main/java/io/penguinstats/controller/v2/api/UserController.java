package io.penguinstats.controller.v2.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.penguinstats.model.User;
import io.penguinstats.service.UserService;
import io.penguinstats.util.CookieUtil;
import io.penguinstats.util.IpUtil;
import io.swagger.annotations.ApiOperation;

@RestController("userController_v2")
@RequestMapping("/api/v2/users")
@Api(tags = {"Account"})
public class UserController {

	public static final String INTERNAL_USER_ID_PREFIX = "internal_";

	private static Logger logger = LogManager.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	@ApiOperation(value = "Login with User ID",
			notes = "Login with the specified User ID.")
	@PostMapping(produces = "text/plain;charset=UTF-8")
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Successfully logged the user in"),
			@ApiResponse(code = 400, message = "User not found")
	})
	public ResponseEntity<String> login(@RequestBody String userID, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			boolean isInternal = false;
			if (userID.startsWith(INTERNAL_USER_ID_PREFIX)) {
				isInternal = true;
				userID = userID.substring(INTERNAL_USER_ID_PREFIX.length());
			}
			User user = userService.getUserByUserID(userID);
			if (user == null) {
				if (isInternal) {
					userID = userService.createNewUser(userID, IpUtil.getIpAddr(request));
					if (userID == null) {
						logger.error("Failed to create new user.");
						return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
					} else {
						userService.addTag(userID, "internal");
					}
				} else {
					return new ResponseEntity<>(HttpStatus.NOT_FOUND);
				}
			}
			CookieUtil.setUserIDCookie(response, userID);
			return new ResponseEntity<>(new JSONObject().put("userID", userID).toString(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error in getUser", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
