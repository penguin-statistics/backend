package io.penguinstats.controller.v1.api;

import io.penguinstats.constant.Constant;
import io.penguinstats.constant.Constant.CustomHeader;
import io.penguinstats.model.User;
import io.penguinstats.service.UserService;
import io.penguinstats.util.CookieUtil;
import io.penguinstats.util.IpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController("userController_v1")
@RequestMapping("/api/users")
@Api(tags = {"@ Deprecated APIs"})
@Deprecated
public class UserController {

	public static final String INTERNAL_USER_ID_PREFIX = "internal_";

	@Autowired
	private UserService userService;

	@ApiOperation("Login")
	@PostMapping(produces = "text/plain;charset=UTF-8")
	public ResponseEntity<String> login(@RequestBody String userID, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		boolean isInternal = false;
		if (userID.startsWith(INTERNAL_USER_ID_PREFIX)) {
			isInternal = true;
			userID = userID.substring(INTERNAL_USER_ID_PREFIX.length());
		}
		User user = userService.getUserByUserID(userID);
		HttpHeaders headers = new HttpHeaders();
		headers.add(CustomHeader.X_PENGUIN_UPGRAGE, Constant.API_V2);
		if (user == null) {
			if (isInternal) {
				userID = userService.createNewUser(userID, IpUtil.getIpAddr(request));
				if (userID != null) {
					userService.addTag(userID, "internal");
				}
			} else {
				return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
			}
		}
		CookieUtil.setUserIDCookie(response, userID);
		return new ResponseEntity<>(new JSONObject().put("userID", userID).toString(), HttpStatus.OK);
	}
}
