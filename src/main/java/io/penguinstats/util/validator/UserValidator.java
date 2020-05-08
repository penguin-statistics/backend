package io.penguinstats.util.validator;

import org.springframework.stereotype.Component;

import io.penguinstats.model.User;
import io.penguinstats.service.UserService;

@Component("userValidator")
public class UserValidator extends BaseValidator {

	private UserService userService;

	public UserValidator(ValidatorContext context, UserService userService) {
		super(context);
		this.userService = userService;
	}

	@Override
	public boolean validate() {
		String userID = this.context.getUserID();

		User user = userService.getUserByUserID(userID);
		if (user == null)
			return false;

		// TODO: check tags here
		//		List<String> tags = Optional.ofNullable(user.getTags()).orElseGet(() -> new ArrayList<>());
		return true;
	}

}
