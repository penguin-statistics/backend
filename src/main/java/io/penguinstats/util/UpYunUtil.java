package io.penguinstats.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import io.penguinstats.enums.ErrorCode;
import io.penguinstats.util.exception.ServiceException;

public class UpYunUtil {

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    private UpYunUtil() {}

    /**
     * Get 'Authorization' for UpYun file uploading
     *
     * @param method Should be POST
     * @param dateTime The timestamp of the request. Must be consistent with the one in the policy. Optional
     * @param path /&lt;bucket&gt;
     * @param policy
     * @param userName Operator's name
     * @param password
     * @param md5 Optional
     * @return
     */
    public static String getAuthorization(String method, Long dateTime, String path, String policy, String userName,
            String password, String md5) {
        StringBuilder sb = new StringBuilder();
        String sp = "&";

        sb.append(method);

        sb.append(sp);
        sb.append(path);

        if (dateTime != null) {
            String date = getDateTimeRFC1123String(dateTime);
            sb.append(sp);
            sb.append(date);
        }

        if (!StringUtils.isEmpty(policy)) {
            sb.append(sp);
            sb.append(policy);
        }

        if (!StringUtils.isEmpty(md5)) {
            sb.append(sp);
            sb.append(md5);
        }

        String raw = sb.toString().trim();
        byte[] hmac = null;
        try {
            hmac = calculateRFC2104HMACRaw(DigestUtils.md5Hex(password).toLowerCase(), raw);
        } catch (Exception e) {
            throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR, "calculate SHA1 wrong.");
        }

        if (hmac != null) {
            return "UPYUN " + userName + ":" + Base64.getEncoder().encodeToString(hmac);
        }

        return null;
    }

    /**
     * Calculate policy
     *
     * @param bucket
     * @param saveKey The path of the saved file. Example: /upload_{random32}{.suffix}
     * @param expiration The expiration of the request. UNIX UTC timestamp. Unit is second.
     * @param notifyUrl The callback URL. Optional
     * @return
     */
    public static String getPolicy(String bucket, String saveKey, Long expiration, String notifyUrl) {
        Map<String, Object> policyMap = new HashMap<>();
        policyMap.put("bucket", bucket);
        policyMap.put("save-key", saveKey);
        policyMap.put("expiration", expiration);
        if (!StringUtils.isEmpty(notifyUrl)) {
            policyMap.put("notify-url", notifyUrl);
        }
        JSONObject obj = new JSONObject(policyMap);
        return Base64.getEncoder().encodeToString(obj.toString().getBytes());
    }

    public static String getDateTimeRFC1123String(Long dateTime) {
        ZonedDateTime now = ZonedDateTime.ofInstant(Instant.ofEpochMilli(dateTime), ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
        return now.format(formatter);
    }

    private static byte[] calculateRFC2104HMACRaw(String key, String data)
            throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] keyBytes = key.getBytes();
        SecretKeySpec signingKey = new SecretKeySpec(keyBytes, HMAC_SHA1_ALGORITHM);

        // Get an hmac_sha1 Mac instance and initialize with the signing key
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        // Compute the hmac on input data bytes
        return mac.doFinal(data.getBytes());
    }

}
