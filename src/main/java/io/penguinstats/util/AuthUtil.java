package io.penguinstats.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.penguinstats.constant.Constant.Auth;
import io.penguinstats.constant.Constant.CustomHeader;
import io.penguinstats.model.User;
import io.penguinstats.service.UserService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component("authUtil")
public class AuthUtil {

    private static AuthUtil authUtil;

    @Autowired
    private UserService userService;
    @Autowired
    private CookieUtil cookieUtil;

    @PostConstruct
    public void init() {
        authUtil = this;
        authUtil.userService = this.userService;
    }

    public String authUserFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        String userID = null;

        if (!StringUtils.isEmpty(authHeader)) {
            String decodedAuth = null;
            try {
                decodedAuth = URLDecoder.decode(authHeader, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error("Error in authUserFromRequest: ", e);
                decodedAuth = authHeader;
            }

            if (!StringUtils.isEmpty(decodedAuth)) {
                decodedAuth = decodedAuth.trim();
                if (decodedAuth.startsWith(Auth.AUTHORIZATION_REALM_PENGUIN_ID)) {
                    userID = StringUtils.substringAfter(decodedAuth, Auth.AUTHORIZATION_REALM_PENGUIN_ID).trim();
                }
            }
        }

        if (StringUtils.isEmpty(userID)) {
            userID = cookieUtil.readUserIDFromCookie(request);
        }

        if (StringUtils.isEmpty(userID)) {
            return null;
        }

        User user = userService.getUserByUserID(userID);
        if (user == null) {
            log.warn("userID " + userID + " is not existed.");
            userID = null;
        } else {
            // old user
            String ip = IpUtil.getIpAddr(request);
            if (ip != null && !user.containsIp(ip)) {
                log.info("Add ip " + ip + " to user " + userID);
                userService.addIP(userID, ip);
            }
        }

        return userID;
    }

    public static void setUserIDHeader(HttpServletResponse response, String userID) {
        response.addHeader(CustomHeader.X_PENGUIN_SET_PENGUIN_ID, userID);
    }

}
