package com.complyt.business.web_hook;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class HmaacGenerator {

    public static String generateHmacSHA256(String secretKey, String message) {
        try {
            String algorithm = "HmacSHA256";

            Mac sha256Hmac = Mac.getInstance(algorithm);

            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), algorithm);
            sha256Hmac.init(secretKeySpec);

            byte[] hmacBytes = sha256Hmac.doFinal(message.getBytes(StandardCharsets.UTF_8));

            return bytesToHex(hmacBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMAC signature", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
