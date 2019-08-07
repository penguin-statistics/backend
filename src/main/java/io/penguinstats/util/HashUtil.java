package io.penguinstats.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {
    public static String getHash(String text) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(text.getBytes(StandardCharsets.UTF_8));
        byte[] digest = md.digest();

        return String.format("%064x", new BigInteger(1, digest));
    }
}
