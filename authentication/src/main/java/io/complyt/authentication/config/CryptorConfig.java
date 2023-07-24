package io.complyt.authentication.config;

import io.complyt.authentication.security.Cryptor;
import io.complyt.authentication.security.CryptorAesCbcPkcs5Padding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Configuration
public class CryptorConfig {

    @Bean("cryptorImpl")
    public Cryptor CryptorImpl(@Value("${cryption.secret-key}") String secretKeyStr) throws NoSuchPaddingException, NoSuchAlgorithmException {
        SecretKey secretKey = convertStringToSecretKey(secretKeyStr);

        return new CryptorAesCbcPkcs5Padding(secretKey);
    }

    private SecretKey convertStringToSecretKey(String secretKeyStr) {
        byte[] decodedSecretKey = Base64.getDecoder().decode(secretKeyStr);
        SecretKey secretKey = new SecretKeySpec(decodedSecretKey, 0, decodedSecretKey.length, "AES");

        return secretKey;
    }
}
