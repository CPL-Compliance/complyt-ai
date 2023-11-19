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
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        Crypto cryptoAesGcmNoPadding = mock(Crypto.class);
        String grantType = "Grant Type";
        String issuerUri = "Issuer Uri";

        // When
        CredentialsService expectedCredentialsService = new CredentialsService(credentialsRepository,
                passwordEncoder, cryptoAesGcmNoPadding, grantType, issuerUri);

        CredentialsService actualCredentialsService = servicesConfig.credentialsService(credentialsRepository,
                passwordEncoder, cryptoAesGcmNoPadding, grantType, issuerUri);

        // Then
        assertEquals(expectedCredentialsService, actualCredentialsService);
    }


    @Test
    void tokenService_createTokenService_returnTokenService() {
        // Given
        TokenRepository tokenRepository = mock(TokenRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        Crypto cryptoAesGcmNoPadding = mock(Crypto.class);
        int tokenExpirationSafeWindowSec = 10;

        // When
        TokenService expectedTokenService = new TokenService(tokenRepository, passwordEncoder,
                cryptoAesGcmNoPadding, tokenExpirationSafeWindowSec);

        TokenService actualTokenService = servicesConfig.tokenService(tokenRepository, passwordEncoder,
                cryptoAesGcmNoPadding, tokenExpirationSafeWindowSec);

        // Then
        assertEquals(expectedTokenService, actualTokenService);
    }

    @Test
    void tokenService_tokenRepositoryIsNull_throwsNullException() {
        // Given
        TokenRepository tokenRepository = mock(TokenRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        Crypto cryptoAesGcmNoPadding = mock(Crypto.class);
        int tokenExpirationSafeWindowSec = 10;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            servicesConfig.tokenService(null, passwordEncoder,
                    cryptoAesGcmNoPadding, tokenExpirationSafeWindowSec);
        });

        assertEquals(nullPointerException.getMessage(), "tokenRepository is marked non-null but is null");
    }

    @Test
    void tokenService_passwordEncoderIsNull_throwsNullException() {
        // Given
        TokenRepository tokenRepository = mock(TokenRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        Crypto cryptoAesGcmNoPadding = mock(Crypto.class);
        int tokenExpirationSafeWindowSec = 10;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            servicesConfig.tokenService(tokenRepository, null,
                    cryptoAesGcmNoPadding, tokenExpirationSafeWindowSec);
        });

        assertEquals(nullPointerException.getMessage(), "passwordEncoder is marked non-null but is null");
    }

    @Test
    void tokenService_cryptoAescryptoAesGcmNoPaddingPkcs5PaddingIsNull_throwsNullException() {
        // Given
        TokenRepository tokenRepository = mock(TokenRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        Crypto cryptoAesGcmNoPadding = mock(Crypto.class);
        int tokenExpirationSafeWindowSec = 10;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            servicesConfig.tokenService(tokenRepository, passwordEncoder,
                    null, tokenExpirationSafeWindowSec);
        });

        assertEquals(nullPointerException.getMessage(), "cryptoAesGcmNoPadding is marked non-null but is null");
    }

    @Test
    void credentialsService_credentialsRepositoryIsNull_throwsNullException() {
        // Given
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        Crypto cryptoAesGcmNoPadding = mock(Crypto.class);
        String grantType = "Grant Type";
        String audience = "audience";

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            servicesConfig.credentialsService(null, passwordEncoder, cryptoAesGcmNoPadding,
                    grantType, audience);
        });

        assertEquals(nullPointerException.getMessage(), "credentialsRepository is marked non-null but is null");
    }

    @Test
    void credentialsService_passwordEncoderIsNull_throwsNullException() {
        // Given
        CredentialsRepository credentialsRepository = mock(CredentialsRepository.class);
        Crypto cryptoAesGcmNoPadding = mock(Crypto.class);
        String grantType = "Grant Type";
        String audience = "audience";

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            servicesConfig.credentialsService(credentialsRepository, null, cryptoAesGcmNoPadding,
                    grantType, audience);
        });

        assertEquals(nullPointerException.getMessage(), "passwordEncoder is marked non-null but is null");
    }

    @Test
    void credentialsService_cryptoAesGcmNoPaddingIsNull_throwsNullException() {
        // Given
        CredentialsRepository credentialsRepository = mock(CredentialsRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        String grantType = "Grant Type";
        String audience = "audience";

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            servicesConfig.credentialsService(credentialsRepository, passwordEncoder, null,
                    grantType, audience);
        });

        assertEquals(nullPointerException.getMessage(), "cryptoAesGcmNoPadding " +
                "is marked non-null but is null");
    }

    @Test
    void credentialsService_grantTypeIsNull_throwsNullException() {
        // Given
        CredentialsRepository credentialsRepository = mock(CredentialsRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        Crypto cryptoAesGcmNoPadding = mock(Crypto.class);
        String audience = "audience";

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            servicesConfig.credentialsService(credentialsRepository, passwordEncoder, cryptoAesGcmNoPadding,
                    null, audience);
        });

        assertEquals(nullPointerException.getMessage(), "grantType is marked non-null but is null");
    }

    @Test
    void credentialsService_issuerUriIsNull_throwsNullException() {
        // Given
        CredentialsRepository credentialsRepository = mock(CredentialsRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        Crypto cryptoAesGcmNoPadding = mock(Crypto.class);
        String grantType = "Grant Type";

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            servicesConfig.credentialsService(credentialsRepository, passwordEncoder, cryptoAesGcmNoPadding,
                    grantType, null);
        });

        assertEquals(nullPointerException.getMessage(), "audience is marked non-null but is null");
    }
}