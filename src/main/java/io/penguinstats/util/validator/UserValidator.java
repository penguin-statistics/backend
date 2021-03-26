package io.penguinstats.util.validator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import io.penguinstats.constant.Constant.UserTag;
import io.penguinstats.model.User;
import io.penguinstats.service.UserService;

@Order(2)
@Component("userValidator")
public class UserValidator extends BaseValidator {

    @Autowired
    private UserService userService;

    @Override
    public boolean validate(ValidatorContext context) {
        String userID = context.getUserID();

        User user = userService.getUserByUserID(userID);
        if (user == null)
            return false;

        Set<String> tags = new HashSet<String>(Optional.ofNullable(user.getTags()).orElseGet(() -> new ArrayList<>()));
        if (tags.contains(UserTag.TESTER) || tags.contains(UserTag.BANNED)) {
            return false;
        }

        return true;
    }

}
