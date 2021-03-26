package io.penguinstats.util.strategy;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.penguinstats.constant.Constant.SystemPropertyKey;
import io.penguinstats.enums.ErrorCode;
import io.penguinstats.service.SystemPropertyService;
import io.penguinstats.util.AESUtil;
import io.penguinstats.util.RSAUtil;
import io.penguinstats.util.exception.BusinessException;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class ScreenshotReportDecryptStrategy implements DecryptStrategy {

    @Autowired
    private SystemPropertyService systemPropertyService;

    @Override
    public String decrypt(String cipherText) {
        String[] strs = cipherText.split(":");
        if (strs.length != 2) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "Failed to parse request body.");
        }
        String encryptedAESKey = strs[0];
        String encryptedBody = strs[1];
        if (StringUtils.isAnyEmpty(encryptedAESKey, encryptedBody)) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "Invalid request.");
        }

        String privateKey = systemPropertyService.getPropertyStringValue(SystemPropertyKey.RECOGNITION_PRIVATE_KEY);
        String ivStr = systemPropertyService.getPropertyStringValue(SystemPropertyKey.AES_IV);
        JSONArray ivArr = new JSONArray(ivStr);
        if (16 != ivArr.length()) {
            log.error("Invalid iv {}", ivStr);
            return null;
        }
        byte[] iv = new byte[16];
        for (int i = 0; i < 16; i++) {
            iv[i] = (byte)ivArr.getInt(i);
        }
        try {
            String decryptedAESKeyBase64 = RSAUtil.decryptDataOnJava(encryptedAESKey, privateKey);
            String decyptedBody = AESUtil.decrypt(encryptedBody, decryptedAESKeyBase64, iv);
            return decyptedBody;
        } catch (Exception e) {
            log.error("Error in decrypt.", e);
            return null;
        }
    }

    @Override
    public DecryptStrategyName getStrategyName() {
        return DecryptStrategyName.SCREENSHOT_REPORT_DECRYPT_STRATEGY;
    }

}
