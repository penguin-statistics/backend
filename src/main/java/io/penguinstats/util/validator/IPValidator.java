package io.penguinstats.util.validator;

import org.springframework.stereotype.Component;

@Component("IPValidator")
public class IPValidator extends BaseValidator {

	public IPValidator(ValidatorContext context) {
		super(context);
	}

	@Override
	public boolean validate() {
		// TODO: check ip ban list
		return true;
	}

}
