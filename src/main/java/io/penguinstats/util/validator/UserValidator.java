package io.penguinstats.util.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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

		// TODO: check tags here
		//		List<String> tags = Optional.ofNullable(user.getTags()).orElseGet(() -> new ArrayList<>());
		return true;
	}

}
