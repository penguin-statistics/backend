package io.penguinstats.util.validator;

import io.penguinstats.configuration.ValidatorConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
public class ValidatorFacade {
    @Autowired
    private ValidatorConfig validatorConfig;

    @Autowired
    private List<BaseValidator> validatorChain;

    public boolean doValid(ValidatorContext context) {
        for (BaseValidator validator : validatorChain) {
            if (!isEnabled(validator)) {
                continue;
            }
            if (!validator.validate(context)) {
                log.warn("Failed to pass " + validator.getClass().getSimpleName() + " check.");
                return false;
            } else {
                log.debug("Pass " + validator.getClass().getSimpleName() + " check.");
            }
        }
        return true;
    }

    private boolean isEnabled(BaseValidator validator) {
        Boolean isEnable = validatorConfig.getConfigMap().get(validator.getClass().getSimpleName());
        return isEnable != null && isEnable;
    }
}
