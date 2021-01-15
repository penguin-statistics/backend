package io.penguinstats.util.validator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class BaseValidator implements Validator {

	@Override
	public abstract boolean validate(ValidatorContext context);

}
