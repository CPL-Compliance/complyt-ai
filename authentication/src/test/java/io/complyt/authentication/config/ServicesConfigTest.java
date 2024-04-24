package io.complyt.authentication.config;

import io.complyt.authentication.business.authorization.AuthorizationServerWrapper;
import io.complyt.authentication.repositories.CredentialsRepository;
import io.complyt.authentication.repositories.TokenRepository;
import io.complyt.authentication.security.Crypto;
import io.complyt.authentication.services.AuthorizationService;
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
        int credentials_expiration_sec = 100;


        // When
        CredentialsService expectedCredentialsService = new CredentialsService(credentialsRepository,
                passwordEncoder, cryptoAesGcmNoPadding, grantType, issuerUri, credentials_expiration_sec);

        CredentialsService actualCredentialsService = servicesConfig.credentialsService(credentialsRepository,
                passwordEncoder, cryptoAesGcmNoPadding, grantType, issuerUri, credentials_expiration_sec);

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
        int credentials_expiration_sec = 100;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            servicesConfig.credentialsService(null, passwordEncoder, cryptoAesGcmNoPadding,
                    grantType, audience, credentials_expiration_sec);
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
        int credentials_expiration_sec = 100;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            servicesConfig.credentialsService(credentialsRepository, null, cryptoAesGcmNoPadding,
                    grantType, audience, credentials_expiration_sec);
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
        int credentials_expiration_sec = 100;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            servicesConfig.credentialsService(credentialsRepository, passwordEncoder, null,
                    grantType, audience, credentials_expiration_sec);
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
        int credentials_expiration_sec = 100;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            servicesConfig.credentialsService(credentialsRepository, passwordEncoder, cryptoAesGcmNoPadding,
                    null, audience, credentials_expiration_sec);
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
        int credentials_expiration_sec = 100;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            servicesConfig.credentialsService(credentialsRepository, passwordEncoder, cryptoAesGcmNoPadding,
                    grantType, null, credentials_expiration_sec);
        });

        assertEquals(nullPointerException.getMessage(), "audience is marked non-null but is null");
    }

    @Test
    void authorizationService_createAuthorizationService_returnAuthorizationService() {
        // Given
        AuthorizationServerWrapper authorizationServerWrapper = mock(AuthorizationServerWrapper.class);
        Crypto cryptoAesGcmNoPadding = mock(Crypto.class);

        // When
        AuthorizationService expectedAuthorizationService = new AuthorizationService(authorizationServerWrapper, cryptoAesGcmNoPadding);

        AuthorizationService actualAuthorizationService = servicesConfig.authorizationService(authorizationServerWrapper, cryptoAesGcmNoPadding);

        // Then
        assertEquals(expectedAuthorizationService, actualAuthorizationService);
    }

    @Test
    void authorizationService_authorizationServerWrapperIsNull_throwsNullException() {
        // Given
        AuthorizationServerWrapper authorizationServerWrapper = mock(AuthorizationServerWrapper.class);
        Crypto cryptoAesGcmNoPadding = mock(Crypto.class);

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            servicesConfig.authorizationService(null, cryptoAesGcmNoPadding);
        });

        assertEquals(nullPointerException.getMessage(), "authorizationServerWrapper is marked non-null but is null");
    }

    @Test
    void authorizationService_cryptoAesGcmNoPaddingIsNull_throwsNullException() {
        // Given
        AuthorizationServerWrapper authorizationServerWrapper = mock(AuthorizationServerWrapper.class);
        Crypto cryptoAesGcmNoPadding = mock(Crypto.class);

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            servicesConfig.authorizationService(authorizationServerWrapper, null);
        });

        assertEquals(nullPointerException.getMessage(), "cryptoAesGcmNoPadding is marked non-null but is null");
    }
}