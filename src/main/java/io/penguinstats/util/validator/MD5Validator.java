package io.penguinstats.util.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import io.penguinstats.service.ItemDropService;

@Order(4)
@Component("MD5Vaildator")
public class MD5Validator extends BaseValidator {

    @Autowired
    private ItemDropService itemDropService;

    @Override
    public boolean validate(ValidatorContext context) {
        String md5 = context.getMd5();
        return StringUtils.isEmpty(md5) || itemDropService.getItemDropsByMD5(md5).isEmpty();
    }

}
