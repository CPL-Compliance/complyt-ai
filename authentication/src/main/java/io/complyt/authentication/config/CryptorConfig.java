package io.complyt.authentication.config;

import io.complyt.authentication.security.Cryptor;
import io.complyt.authentication.security.CryptorAesCbcPkcs5Padding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CryptorConfig {

    @Bean("cryptorImpl")
    public Cryptor CryptorImpl(@Value("${cryption.secret-key}") String secretKey){
        return new CryptorAesCbcPkcs5Padding(secretKey);
    }
}
