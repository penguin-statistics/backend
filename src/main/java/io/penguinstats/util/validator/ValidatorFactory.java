package io.penguinstats.util.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.penguinstats.enums.ValidatorType;
import io.penguinstats.service.DropInfoService;
import io.penguinstats.service.UserService;

@Component("validatorFactory")
public class ValidatorFactory {

	@Autowired
	private DropInfoService dropInfoService;

	@Autowired
	private UserService userService;

	public Validator getValidator(ValidatorType type, ValidatorContext context) throws Exception {
		switch (type) {
			case STAGE_TIME:
				return new StageTimeValidator(context, dropInfoService);
			case USER:
				return new UserValidator(context, userService);
			case IP:
				return new IPValidator(context);
			case DROPS:
				return new DropsValidator(context, dropInfoService);
			default:
				throw new Exception("Failed to create validator for " + type);
		}
	}

}
