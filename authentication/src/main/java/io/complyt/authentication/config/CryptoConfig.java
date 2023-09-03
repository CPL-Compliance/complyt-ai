package io.complyt.authentication.config;

import io.complyt.authentication.security.Crypto;
import io.complyt.authentication.security.CryptoAesCbcPkcs5Padding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class CryptoConfig {

    @Bean("cryptoAesCbcPkcs5Padding")
    public Crypto cryptoAesCbcPkcs5Padding(@Value("${crypto.secret-key}") String secretKeyStr) {
        SecretKey secretKey = convertStringToSecretKey(secretKeyStr);

        return new CryptoAesCbcPkcs5Padding(secretKey);
    }

    private SecretKey convertStringToSecretKey(String secretKeyStr) {
        byte[] decodedSecretKey = Base64.getDecoder().decode(secretKeyStr);

        return new SecretKeySpec(decodedSecretKey, 0, decodedSecretKey.length, "AES");
    }
}
