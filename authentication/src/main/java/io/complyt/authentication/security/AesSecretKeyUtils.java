package io.complyt.authentication.security;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AesSecretKeyUtils {
    static String keyGeneratorAlgorithm = "AES";

    /* Generating Secret key */

    // Generating Secret Key using KeyGenerator class
    public static SecretKey generateAesKey(int n) {
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(keyGeneratorAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

        keyGenerator.init(n);
        SecretKey originalKey = keyGenerator.generateKey();

        return originalKey;
    }

    // Generating Secret Key using password and salt
    public static SecretKey getKeyFromPassword(@NonNull String password, @NonNull String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey originalKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");

        return originalKey;
    }

    /* Converting Secret key into String */
    public static String convertSecretKeyToString(@NonNull SecretKey secretKey) {
        // Converting the Secret Key into byte array
        byte[] rawData = secretKey.getEncoded();
        // Getting String - Base64 encoded version of the Secret Key

        return Base64.getEncoder().encodeToString(rawData);
    }

    /* Converting String into Secret key into */
    public static SecretKey convertStringToSecretKey(@NonNull String encodedKey) {
        // Decoding the Base64 encoded string into byte array
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        // Rebuilding the Secret Key using SecretKeySpec Class
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

        return originalKey;
    }
}