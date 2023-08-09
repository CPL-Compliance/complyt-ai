package io.complyt.authentication.config;

import io.complyt.authentication.repositories.CredentialsRepository;
import io.complyt.authentication.repositories.TokenRepository;
import io.complyt.authentication.security.Crypto;
import io.complyt.authentication.services.CredentialsService;
import io.complyt.authentication.services.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class ServicesConfigTest {

    ServicesConfig servicesConfig;

    @BeforeEach
    void setUp() {
        servicesConfig = new ServicesConfig();
    }

    @Test
    void credentialsService_createCredentialsService_returnCredentialsService() {
        // Given
        CredentialsRepository credentialsRepository = mock(CredentialsRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        Crypto cryptoAesCbcPkcs5Padding = mock(Crypto.class);
        String grantType = "Grant Type";
        String issuerUri = "Issuer Uri";

        // When
        CredentialsService expectedCredentialsService = new CredentialsService(credentialsRepository, passwordEncoder, cryptoAesCbcPkcs5Padding, grantType, issuerUri);
        CredentialsService actualCredentialsService = servicesConfig.credentialsService(credentialsRepository, passwordEncoder, cryptoAesCbcPkcs5Padding, grantType, issuerUri);

        // Then
        assertEquals(expectedCredentialsService, actualCredentialsService);
    }


    @Test
    void tokenService_createTokenService_returnTokenService() {
        // Given
        TokenRepository tokenRepository = mock(TokenRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        Crypto cryptoAesCbcPkcs5Padding = mock(Crypto.class);
        int tokenExpirationSafeWindowSec = 10;

        // When
        TokenService expectedTokenService = new TokenService(tokenRepository, passwordEncoder, cryptoAesCbcPkcs5Padding, tokenExpirationSafeWindowSec);
        TokenService actualTokenService = servicesConfig.tokenService(tokenRepository, passwordEncoder, cryptoAesCbcPkcs5Padding, tokenExpirationSafeWindowSec);

        // Then
        assertEquals(expectedTokenService, actualTokenService);
    }
}