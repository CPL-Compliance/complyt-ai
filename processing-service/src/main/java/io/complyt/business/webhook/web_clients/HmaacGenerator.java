package io.complyt.business.webhook.web_clients;

import io.complyt.annotations.Generated;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Generated
public class HmaacGenerator {

    /**
     * Generates an HMAC SHA-256 signature from a secret key and message.
     *
     * @param secretKey the secret key used for signing
     * @param message the message to sign
     * @return the HMAC SHA-256 signature in hexadecimal format
     */
    public static String generateHmacSHA256(String secretKey, String message) {
        try {
            String algorithm = "HmacSHA256"; // Specify the hashing algorithm

            // Create a Mac instance using the HmacSHA256 algorithm
            Mac sha256Hmac = Mac.getInstance(algorithm);

            // Convert the secret key to bytes and create a secret key spec
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), algorithm);

            // Initialize the Mac instance with the secret key
            sha256Hmac.init(secretKeySpec);

            // Compute the HMAC on the input message
            byte[] hmacBytes = sha256Hmac.doFinal(message.getBytes(StandardCharsets.UTF_8));

            // Convert the byte array result to a hexadecimal string
            return bytesToHex(hmacBytes);
        } catch (Exception e) {
            // Wrap and rethrow any exception as a runtime exception
            throw new RuntimeException("Failed to generate HMAC signature", e);
        }
    }

    /**
     * Converts a byte array to its hexadecimal string representation.
     *
     * @param bytes the byte array to convert
     * @return a hexadecimal string representation of the byte array
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b); // Mask byte to unsigned int and convert to hex
            if (hex.length() == 1) hexString.append('0'); // Pad single-digit hex with leading zero
            hexString.append(hex);
        }
        return hexString.toString();
    }

}