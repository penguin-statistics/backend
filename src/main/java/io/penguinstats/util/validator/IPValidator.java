package io.penguinstats.util.validator;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(3)
@Component("IPValidator")
public class IPValidator extends BaseValidator {


    @Override
    public boolean validate(ValidatorContext context) {
        // TODO: check ip ban list
        return true;
    }

}
