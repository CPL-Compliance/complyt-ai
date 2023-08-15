package io.complyt.authentication.config;

import io.complyt.authentication.repositories.CredentialsRepository;
import io.complyt.authentication.repositories.TokenRepository;
import io.complyt.authentication.security.Crypto;
import io.complyt.authentication.services.CredentialsService;
import io.complyt.authentication.services.TokenService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ServicesConfig {
    @Bean
    CredentialsService credentialsService(@NonNull CredentialsRepository credentialsRepository,
                                          @NonNull PasswordEncoder passwordEncoder,
                                          @NonNull Crypto cryptoAesCbcPkcs5Padding,
                                          @NonNull @Value("${authorization.grant-type}") String grantType,
                                          @NonNull @Value("${authorization.audience}") String audience) {
        return new CredentialsService(credentialsRepository, passwordEncoder, cryptoAesCbcPkcs5Padding, grantType,
                audience);
    }

    @Bean
    TokenService tokenService(@NonNull TokenRepository tokenRepository,
                              @NonNull PasswordEncoder passwordEncoder,
                              @NonNull Crypto cryptoAesCbcPkcs5Padding,
                              @Value("${token-service.token-expiration-safe-window-sec}") int tokenExpirationSafeWindowSec) {
        return new TokenService(tokenRepository, passwordEncoder, cryptoAesCbcPkcs5Padding,
                tokenExpirationSafeWindowSec);
    }
}
