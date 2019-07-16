package io.penguinstats.api;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import io.penguinstats.api.filter.annotation.SetUserIDCookie;
import io.penguinstats.bean.User;
import io.penguinstats.service.UserService;
import io.penguinstats.util.APIUtil;

@Path("/user")
public class UserAPI {

	public static final String INTERNAL_USER_ID_PREFIX = "internal_";

	private static final UserService userService = UserService.getInstance();

	private static Logger logger = LogManager.getLogger(UserAPI.class);

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@SetUserIDCookie
	public Response login(@Context HttpServletRequest request, InputStream requestBodyStream) {
		try {
			String jsonString = APIUtil.convertStreamToString(requestBodyStream);
			JSONObject obj = new JSONObject(jsonString);
			if (!obj.has("userID") || obj.isNull("userID"))
				return Response.status(Status.BAD_REQUEST).build();
			String userID = obj.getString("userID");
			boolean isInternal = false;
			if (userID.startsWith(INTERNAL_USER_ID_PREFIX)) {
				isInternal = true;
				userID = userID.substring(INTERNAL_USER_ID_PREFIX.length());
			}
			User user = userService.getUser(userID);
			if (user == null) {
				if (isInternal) {
					userID = userService.createNewUser(userID, APIUtil.getClientIp(request));
					if (userID == null) {
						logger.error("Failed to create new user.");
						return Response.status(Status.INTERNAL_SERVER_ERROR).build();
					} else {
						userService.addTag(userID, "internal");
						user = userService.getUser(userID);
					}
				} else {
					return Response.status(Status.NOT_FOUND).build();
				}
			}
			APIUtil.setUserIDInSession(request, userID);
			JSONObject userObj = user.asJSON();
			userObj.remove("weight");
			return Response.ok(userObj.toString()).build();
		} catch (Exception e) {
			logger.error("Error in getUser", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

}
