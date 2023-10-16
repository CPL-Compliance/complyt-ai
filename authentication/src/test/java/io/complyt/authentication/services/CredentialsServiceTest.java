package io.complyt.authentication.services;

import io.complyt.authentication.domain.ApiKey;
import io.complyt.authentication.domain.Credentials;
import io.complyt.authentication.repositories.CredentialsRepository;
import io.complyt.authentication.security.Crypto;
import io.complyt.authentication.security.EncryptedData;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import test_utils.unit_tests.TestUtilities;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
class CredentialsServiceTest {
    CredentialsService credentialsService;

    @Mock
    CredentialsRepository credentialsRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    Crypto cryptoAesCbcPkcs5Padding;

    @BeforeEach
    void setUp() {
        credentialsService = new CredentialsService(credentialsRepository, passwordEncoder, cryptoAesCbcPkcs5Padding,
                "grantType", "audience");
    }

    @Test
    void getCredentialsByApiKey_failedAuthentication_returnsMonoEmpty() {
        // Given
        Credentials credentials = TestUtilities.createCredentials();
        ApiKey apiKey = TestUtilities.createApiKey();

        // When
        when(credentialsRepository.findByComplytClientId(apiKey.getClientId())).thenReturn(Mono.just(credentials));
        when(passwordEncoder.matches(apiKey.getClientSecret(), credentials.getComplytClientSecret()))
                .thenReturn(false);

        // Then
        Mono<Credentials> credentialsByApiKeyMono = credentialsService.getCredentialsByApiKey(apiKey);
        StepVerifier.create(credentialsByApiKeyMono).verifyComplete();
    }

    @Test
    void getCredentialsByApiKey_noDocumentWithClientId_returnsMonoEmpty() {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();

        // When
        when(credentialsRepository.findByComplytClientId(apiKey.getClientId())).thenReturn(Mono.empty());

        // Then
        Mono<Credentials> credentialsByApiKeyMono = credentialsService.getCredentialsByApiKey(apiKey);
        StepVerifier.create(credentialsByApiKeyMono).verifyComplete();
    }

    @Test
    void getCredentialsByApiKey_credentialsExsits_returnsCredentials()
            throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException,
            BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();
        Credentials decryptedCreds = TestUtilities.createDecryptedCreds(credentials);

        // When
        when(credentialsRepository.findByComplytClientId(apiKey.getClientId())).thenReturn(Mono.just(credentials));
        when(passwordEncoder.matches(apiKey.getClientSecret(), credentials.getComplytClientSecret()))
                .thenReturn(true);
        when(cryptoAesCbcPkcs5Padding.decrypt(new EncryptedData(credentials.getClientIdIv(),
                credentials.getClientId()))).thenReturn(credentials.getClientId());
        when(cryptoAesCbcPkcs5Padding.decrypt(new EncryptedData(credentials.getClientSecretIv(),
                credentials.getClientSecret()))).thenReturn(credentials.getClientSecret());

        // Then
        Mono<Credentials> credentialsByApiKeyMono = credentialsService.getCredentialsByApiKey(apiKey);
        StepVerifier.create(credentialsByApiKeyMono).expectNext(decryptedCreds).verifyComplete();
    }

    @Test
    void getCredentialsByApiKey_decryptionThrowsBadPaddingException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException,
            BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();

        // When
        when(credentialsRepository.findByComplytClientId(apiKey.getClientId())).thenReturn(Mono.just(credentials));
        when(passwordEncoder.matches(apiKey.getClientSecret(), credentials.getComplytClientSecret()))
                .thenReturn(true);
        when(cryptoAesCbcPkcs5Padding.decrypt(new EncryptedData(credentials.getClientIdIv(),
                credentials.getClientId()))).thenThrow(new BadPaddingException("Error"));

        // Then
        Mono<Credentials> credentialsMono = credentialsService.getCredentialsByApiKey(apiKey);

        StepVerifier.create(credentialsMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void getCredentialsByApiKey_decryptionThrowsIllegalBlockSizeException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException,
            BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();

        // When
        when(credentialsRepository.findByComplytClientId(apiKey.getClientId())).thenReturn(Mono.just(credentials));
        when(passwordEncoder.matches(apiKey.getClientSecret(), credentials.getComplytClientSecret()))
                .thenReturn(true);
        when(cryptoAesCbcPkcs5Padding.decrypt(new EncryptedData(credentials.getClientIdIv(),
                credentials.getClientId()))).thenThrow(new IllegalBlockSizeException("Error"));

        // Then
        Mono<Credentials> credentialsMono = credentialsService.getCredentialsByApiKey(apiKey);

        StepVerifier.create(credentialsMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void getCredentialsByApiKey_decryptionThrowsInvalidAlgorithmParameterException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException,
            BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();

        // When
        when(credentialsRepository.findByComplytClientId(apiKey.getClientId())).thenReturn(Mono.just(credentials));
        when(passwordEncoder.matches(apiKey.getClientSecret(), credentials.getComplytClientSecret()))
                .thenReturn(true);
        when(cryptoAesCbcPkcs5Padding.decrypt(new EncryptedData(credentials.getClientIdIv(),
                credentials.getClientId()))).thenThrow(new InvalidAlgorithmParameterException("Error"));

        // Then
        Mono<Credentials> credentialsMono = credentialsService.getCredentialsByApiKey(apiKey);

        StepVerifier.create(credentialsMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void getCredentialsByApiKey_decryptionThrowsInvalidKeyException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException,
            BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();

        // When
        when(credentialsRepository.findByComplytClientId(apiKey.getClientId())).thenReturn(Mono.just(credentials));
        when(passwordEncoder.matches(apiKey.getClientSecret(), credentials.getComplytClientSecret()))
                .thenReturn(true);
        when(cryptoAesCbcPkcs5Padding.decrypt(new EncryptedData(credentials.getClientIdIv(),
                credentials.getClientId()))).thenThrow(new InvalidKeyException("Error"));

        // Then
        Mono<Credentials> credentialsMono = credentialsService.getCredentialsByApiKey(apiKey);

        StepVerifier.create(credentialsMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void getCredentialsByApiKey_decryptionThrowsNoSuchPaddingException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException,
            BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();

        // When
        when(credentialsRepository.findByComplytClientId(apiKey.getClientId())).thenReturn(Mono.just(credentials));
        when(passwordEncoder.matches(apiKey.getClientSecret(), credentials.getComplytClientSecret()))
                .thenReturn(true);
        when(cryptoAesCbcPkcs5Padding.decrypt(new EncryptedData(credentials.getClientIdIv(),
                credentials.getClientId()))).thenThrow(new NoSuchPaddingException("Error"));

        // Then
        Mono<Credentials> credentialsMono = credentialsService.getCredentialsByApiKey(apiKey);

        StepVerifier.create(credentialsMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void getCredentialsByApiKey_decryptionThrowsNoSuchAlgorithmException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException,
            BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();

        // When
        when(credentialsRepository.findByComplytClientId(apiKey.getClientId())).thenReturn(Mono.just(credentials));
        when(passwordEncoder.matches(apiKey.getClientSecret(), credentials.getComplytClientSecret()))
                .thenReturn(true);
        when(cryptoAesCbcPkcs5Padding.decrypt(new EncryptedData(credentials.getClientIdIv(),
                credentials.getClientId()))).thenThrow(new NoSuchAlgorithmException("Error"));

        // Then
        Mono<Credentials> credentialsMono = credentialsService.getCredentialsByApiKey(apiKey);

        StepVerifier.create(credentialsMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void saveCredentials_validCredentialsAndValidApiKey_returnsSavedCredentials()
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();
        EncryptedData clientIdEncryptedData = new EncryptedData("clientIdIv", "clientIdCipherText");
        EncryptedData clientSecretEncryptedData = new EncryptedData("clientSecretIv",
                "clientSecretCipherText");

        Credentials encryptedCredentials = TestUtilities.createEncryptedCredentials(apiKey, clientIdEncryptedData,
                clientSecretEncryptedData, "encoded");

        // When
        when(cryptoAesCbcPkcs5Padding.encrypt(credentials.getClientId())).thenReturn(clientIdEncryptedData);
        when(cryptoAesCbcPkcs5Padding.encrypt(credentials.getClientSecret())).thenReturn(clientSecretEncryptedData);
        when(passwordEncoder.encode(apiKey.getClientSecret())).thenReturn("encoded");
        when(credentialsRepository.save(encryptedCredentials)).thenReturn(Mono.just(encryptedCredentials));

        // Then
        Mono<Credentials> credentialsMono = credentialsService.saveCredentials(credentials, apiKey);

        StepVerifier.create(credentialsMono).expectNext(encryptedCredentials).verifyComplete();
    }

    @Test
    void saveCredentials_encryptionThrowsNoSuchPaddingException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();

        // When
        when(cryptoAesCbcPkcs5Padding.encrypt(credentials.getClientId()))
                .thenThrow(new NoSuchPaddingException("Error"));

        // Then
        Mono<Credentials> credentialsMono = credentialsService.saveCredentials(credentials, apiKey);

        StepVerifier.create(credentialsMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void saveCredentials_encryptionThrowsNoSuchAlgorithmException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();

        // When
        when(cryptoAesCbcPkcs5Padding.encrypt(credentials.getClientId()))
                .thenThrow(new NoSuchAlgorithmException("Error"));

        // Then
        Mono<Credentials> credentialsMono = credentialsService.saveCredentials(credentials, apiKey);

        StepVerifier.create(credentialsMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void saveCredentials_encryptionThrowsInvalidAlgorithmParameterException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();

        // When
        when(cryptoAesCbcPkcs5Padding.encrypt(credentials.getClientId()))
                .thenThrow(new InvalidAlgorithmParameterException("Error"));

        // Then
        Mono<Credentials> credentialsMono = credentialsService.saveCredentials(credentials, apiKey);

        StepVerifier.create(credentialsMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void saveCredentials_encryptionThrowsInvalidKeyException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();

        // When
        when(cryptoAesCbcPkcs5Padding.encrypt(credentials.getClientId()))
                .thenThrow(new InvalidKeyException("Error"));

        // Then
        Mono<Credentials> credentialsMono = credentialsService.saveCredentials(credentials, apiKey);

        StepVerifier.create(credentialsMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void saveCredentials_encryptionThrowsBadPaddingException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();

        // When
        when(cryptoAesCbcPkcs5Padding.encrypt(credentials.getClientId()))
                .thenThrow(new BadPaddingException("Error"));

        // Then
        Mono<Credentials> credentialsMono = credentialsService.saveCredentials(credentials, apiKey);

        StepVerifier.create(credentialsMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void saveCredentials_encryptionThrowsIllegalBlockSizeException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();

        // When
        when(cryptoAesCbcPkcs5Padding.encrypt(credentials.getClientId()))
                .thenThrow(new IllegalBlockSizeException("Error"));

        // Then
        Mono<Credentials> credentialsMono = credentialsService.saveCredentials(credentials, apiKey);

        StepVerifier.create(credentialsMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void getCredentialsByApiKey_apiKeyIsNull_throwsNullException() {
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            credentialsService.getCredentialsByApiKey(null);
        });

        assertEquals(nullPointerException.getMessage(), "apiKey is marked non-null but is null");
    }

    @Test
    void saveCredentials_apiKeyIsNull_throwsNullException() {
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            credentialsService.saveCredentials(TestUtilities.createCredentials(), null);
        });

        assertEquals(nullPointerException.getMessage(), "apiKey is marked non-null but is null");
    }

    @Test
    void saveCredentials_credentialsIsNull_throwsNullException() {
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            credentialsService.saveCredentials(null, TestUtilities.createApiKey());
        });

        assertEquals(nullPointerException.getMessage(), "credentials is marked non-null but is null");
    }
}