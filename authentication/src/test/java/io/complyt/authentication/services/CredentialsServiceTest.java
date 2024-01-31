package io.complyt.authentication.services;

import io.complyt.authentication.domain.ApiKey;
import io.complyt.authentication.domain.Credentials;
import io.complyt.authentication.domain.enums.ApiKeyStatus;
import io.complyt.authentication.repositories.CredentialsRepository;
import io.complyt.authentication.security.Crypto;
import io.complyt.authentication.security.EncryptedData;
import io.complyt.authentication.v1.exceptions.types.ApiKeyNotValidException;
import lombok.NonNull;
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
import java.time.Duration;

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
    Crypto cryptoAesGcmNoPadding;

    @BeforeEach
    void setUp() {
        String grantType = "grantType";
        String audience = "audience";
        credentialsService = new CredentialsService(credentialsRepository, passwordEncoder, cryptoAesGcmNoPadding,
                grantType, audience);
    }

    @Test
    void test(){}

    @Test
    void getCredentialsByApiKeyAndDecrypt_credentialsExistsWithStatusCancelled_returnsMonoEmpty() {
        // Given
        Credentials credentials = TestUtilities.createCredentials();
        Credentials cancelledCcredentials = credentials.withStatus(ApiKeyStatus.CANCELLED);

        ApiKey apiKey = TestUtilities.createApiKey();

        // When
        when(credentialsRepository.findByComplytClientId(apiKey.clientId())).thenReturn(Mono.just(cancelledCcredentials));
        when(passwordEncoder.matches(apiKey.clientSecret(), credentials.getComplytClientSecret())).thenReturn(true);

        // Then
        Mono<Credentials> credentialsByApiKeyMono = credentialsService.getCredentialsByApiKeyAndDecrypt(apiKey);
        StepVerifier.create(credentialsByApiKeyMono).verifyComplete();
    }

    @Test
    void getCredentialsByApiKeyAndDecrypt_noDocumentWithClientId_returnsMonoEmpty() {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();

        // When
        when(credentialsRepository.findByComplytClientId(apiKey.clientId())).thenReturn(Mono.empty());

        // Then
        Mono<Credentials> credentialsByApiKeyMono = credentialsService.getCredentialsByApiKeyAndDecrypt(apiKey);
        StepVerifier.create(credentialsByApiKeyMono).verifyComplete();
    }

    @Test
    void getCredentialsByApiKeyAndDecrypt_credentialsExists_returnsCredentials()
            throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException,
            BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();
        Credentials decryptedCreds = TestUtilities.createDecryptedCreds(credentials);

        // When
        when(credentialsRepository.findByComplytClientId(apiKey.clientId())).thenReturn(Mono.just(credentials));
        when(passwordEncoder.matches(apiKey.clientSecret(), credentials.getComplytClientSecret()))
                .thenReturn(true);
        when(cryptoAesGcmNoPadding.decrypt(new EncryptedData(credentials.getClientIdIv(),
                credentials.getClientId()))).thenReturn(credentials.getClientId());
        when(cryptoAesGcmNoPadding.decrypt(new EncryptedData(credentials.getClientSecretIv(),
                credentials.getClientSecret()))).thenReturn(credentials.getClientSecret());

        // Then
        Mono<Credentials> credentialsByApiKeyMono = credentialsService.getCredentialsByApiKeyAndDecrypt(apiKey);
        StepVerifier.create(credentialsByApiKeyMono).expectNext(decryptedCreds).verifyComplete();
    }

    @Test
    void getCredentialsByApiKeyAndDecrypt_decryptionThrowsBadPaddingException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException,
            BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();

        // When
        when(credentialsRepository.findByComplytClientId(apiKey.clientId())).thenReturn(Mono.just(credentials));
        when(passwordEncoder.matches(apiKey.clientSecret(), credentials.getComplytClientSecret()))
                .thenReturn(true);
        when(cryptoAesGcmNoPadding.decrypt(new EncryptedData(credentials.getClientIdIv(),
                credentials.getClientId()))).thenThrow(new BadPaddingException("Error"));

        // Then
        Mono<Credentials> credentialsMono = credentialsService.getCredentialsByApiKeyAndDecrypt(apiKey);

        StepVerifier.create(credentialsMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void getCredentialsByApiKeyAndDecrypt_decryptionThrowsIllegalBlockSizeException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException,
            BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();

        // When
        when(credentialsRepository.findByComplytClientId(apiKey.clientId())).thenReturn(Mono.just(credentials));
        when(passwordEncoder.matches(apiKey.clientSecret(), credentials.getComplytClientSecret()))
                .thenReturn(true);
        when(cryptoAesGcmNoPadding.decrypt(new EncryptedData(credentials.getClientIdIv(),
                credentials.getClientId()))).thenThrow(new IllegalBlockSizeException("Error"));

        // Then
        Mono<Credentials> credentialsMono = credentialsService.getCredentialsByApiKeyAndDecrypt(apiKey);

        StepVerifier.create(credentialsMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void getCredentialsByApiKeyAndDecrypt_decryptionThrowsInvalidAlgorithmParameterException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException,
            BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();

        // When
        when(credentialsRepository.findByComplytClientId(apiKey.clientId())).thenReturn(Mono.just(credentials));
        when(passwordEncoder.matches(apiKey.clientSecret(), credentials.getComplytClientSecret()))
                .thenReturn(true);
        when(cryptoAesGcmNoPadding.decrypt(new EncryptedData(credentials.getClientIdIv(),
                credentials.getClientId()))).thenThrow(new InvalidAlgorithmParameterException("Error"));

        // Then
        Mono<Credentials> credentialsMono = credentialsService.getCredentialsByApiKeyAndDecrypt(apiKey);

        StepVerifier.create(credentialsMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void getCredentialsByApiKeyAndDecrypt_decryptionThrowsInvalidKeyException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException,
            BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();

        // When
        when(credentialsRepository.findByComplytClientId(apiKey.clientId())).thenReturn(Mono.just(credentials));
        when(passwordEncoder.matches(apiKey.clientSecret(), credentials.getComplytClientSecret()))
                .thenReturn(true);
        when(cryptoAesGcmNoPadding.decrypt(new EncryptedData(credentials.getClientIdIv(),
                credentials.getClientId()))).thenThrow(new InvalidKeyException("Error"));

        // Then
        Mono<Credentials> credentialsMono = credentialsService.getCredentialsByApiKeyAndDecrypt(apiKey);

        StepVerifier.create(credentialsMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void getCredentialsByApiKeyAndDecrypt_decryptionThrowsNoSuchPaddingException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException,
            BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();

        // When
        when(credentialsRepository.findByComplytClientId(apiKey.clientId())).thenReturn(Mono.just(credentials));
        when(passwordEncoder.matches(apiKey.clientSecret(), credentials.getComplytClientSecret()))
                .thenReturn(true);
        when(cryptoAesGcmNoPadding.decrypt(new EncryptedData(credentials.getClientIdIv(),
                credentials.getClientId()))).thenThrow(new NoSuchPaddingException("Error"));

        // Then
        Mono<Credentials> credentialsMono = credentialsService.getCredentialsByApiKeyAndDecrypt(apiKey);

        StepVerifier.create(credentialsMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void getCredentialsByApiKeyAndDecrypt_decryptionThrowsNoSuchAlgorithmException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException,
            BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();

        // When
        when(credentialsRepository.findByComplytClientId(apiKey.clientId())).thenReturn(Mono.just(credentials));
        when(passwordEncoder.matches(apiKey.clientSecret(), credentials.getComplytClientSecret()))
                .thenReturn(true);
        when(cryptoAesGcmNoPadding.decrypt(new EncryptedData(credentials.getClientIdIv(),
                credentials.getClientId()))).thenThrow(new NoSuchAlgorithmException("Error"));

        // Then
        Mono<Credentials> credentialsMono = credentialsService.getCredentialsByApiKeyAndDecrypt(apiKey);

        StepVerifier.create(credentialsMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void saveCredentials_validCredentialsAndValidApiKey_returnsSavedCredentials()
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();
        String tenantId = TestUtilities.tenantId;
        String name = TestUtilities.name;
        EncryptedData clientIdEncryptedData = new EncryptedData("clientIdIv", "clientIdCipherText");
        EncryptedData clientSecretEncryptedData = new EncryptedData("clientSecretIv",
                "clientSecretCipherText");

        Credentials encryptedCredentials = TestUtilities.createEncryptedCredentials(apiKey, clientIdEncryptedData,
                        clientSecretEncryptedData, "encoded")
                .withStatus(ApiKeyStatus.ACTIVE);

        // When
        when(cryptoAesGcmNoPadding.encrypt(credentials.getClientId())).thenReturn(clientIdEncryptedData);
        when(cryptoAesGcmNoPadding.encrypt(credentials.getClientSecret())).thenReturn(clientSecretEncryptedData);
        when(passwordEncoder.encode(apiKey.clientSecret())).thenReturn("encoded");
        when(credentialsRepository.save(encryptedCredentials)).thenReturn(Mono.just(encryptedCredentials));

        // Then
        Mono<Credentials> credentialsMono = credentialsService.saveCredentials(credentials, apiKey, tenantId, name);

        StepVerifier.create(credentialsMono).expectNext(encryptedCredentials).verifyComplete();
    }

    @Test
    void saveCredentials_encryptionThrowsNoSuchPaddingException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();
        String tenantId = TestUtilities.tenantId;
        String name = TestUtilities.name;

        // When
        when(cryptoAesGcmNoPadding.encrypt(credentials.getClientId()))
                .thenThrow(new NoSuchPaddingException("Error"));

        // Then
        Mono<Credentials> credentialsMono = credentialsService.saveCredentials(credentials, apiKey, tenantId, name);

        StepVerifier.create(credentialsMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void saveCredentials_encryptionThrowsNoSuchAlgorithmException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();
        String tenantId = TestUtilities.tenantId;
        String name = TestUtilities.name;

        // When
        when(cryptoAesGcmNoPadding.encrypt(credentials.getClientId()))
                .thenThrow(new NoSuchAlgorithmException("Error"));

        // Then
        Mono<Credentials> credentialsMono = credentialsService.saveCredentials(credentials, apiKey, tenantId, name);

        StepVerifier.create(credentialsMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void saveCredentials_encryptionThrowsInvalidAlgorithmParameterException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();
        String tenantId = TestUtilities.tenantId;
        String name = TestUtilities.name;

        // When
        when(cryptoAesGcmNoPadding.encrypt(credentials.getClientId()))
                .thenThrow(new InvalidAlgorithmParameterException("Error"));

        // Then
        Mono<Credentials> credentialsMono = credentialsService.saveCredentials(credentials, apiKey, tenantId, name);

        StepVerifier.create(credentialsMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void saveCredentials_encryptionThrowsInvalidKeyException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();
        String tenantId = TestUtilities.tenantId;
        String name = TestUtilities.name;

        // When
        when(cryptoAesGcmNoPadding.encrypt(credentials.getClientId()))
                .thenThrow(new InvalidKeyException("Error"));

        // Then
        Mono<Credentials> credentialsMono = credentialsService.saveCredentials(credentials, apiKey, tenantId, name);

        StepVerifier.create(credentialsMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void saveCredentials_encryptionThrowsBadPaddingException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();
        String tenantId = TestUtilities.tenantId;
        String name = TestUtilities.name;

        // When
        when(cryptoAesGcmNoPadding.encrypt(credentials.getClientId()))
                .thenThrow(new BadPaddingException("Error"));

        // Then
        Mono<Credentials> credentialsMono = credentialsService.saveCredentials(credentials, apiKey, tenantId, name);

        StepVerifier.create(credentialsMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void saveCredentials_encryptionThrowsIllegalBlockSizeException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();
        String tenantId = TestUtilities.tenantId;
        String name = TestUtilities.name;

        // When
        when(cryptoAesGcmNoPadding.encrypt(credentials.getClientId()))
                .thenThrow(new IllegalBlockSizeException("Error"));

        // Then
        Mono<Credentials> credentialsMono = credentialsService.saveCredentials(credentials, apiKey, tenantId, name);

        StepVerifier.create(credentialsMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void markAsCancelled_credentialsFoundAndCancelled_returnCredentials(){
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();
        Credentials cancelledCredentials = credentials.withStatus(ApiKeyStatus.CANCELLED);

        //When
        when(credentialsRepository.markAsCancelled(apiKey.clientId())).thenReturn(Mono.just(cancelledCredentials));

        //Then
        Mono<Credentials> credentialsMono = credentialsService.markAsCancelled(apiKey);
        StepVerifier.create(credentialsMono).expectNext(cancelledCredentials).verifyComplete();

    }

    @Test
    void getCredentialsByApiKeyAndDecrypt_apiKeyIsNull_throwsNullException() {
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            credentialsService.getCredentialsByApiKeyAndDecrypt(null);
        });

        assertEquals(nullPointerException.getMessage(), "apiKey is marked non-null but is null");
    }

    @Test
    void saveCredentials_apiKeyIsNull_throwsNullException() {
        String tenantId = TestUtilities.tenantId;
        String name = TestUtilities.name;

        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            credentialsService.saveCredentials(TestUtilities.createCredentials(), null, tenantId, name);
        });

        assertEquals(nullPointerException.getMessage(), "apiKey is marked non-null but is null");
    }

    @Test
    void saveCredentials_credentialsIsNull_throwsNullException() {
        String tenantId = TestUtilities.tenantId;
        String name = TestUtilities.name;

        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            credentialsService.saveCredentials(null, TestUtilities.createApiKey(), tenantId, name);
        });

        assertEquals(nullPointerException.getMessage(), "credentials is marked non-null but is null");
    }

    @Test
    void markAsCancelled_apiKeyIsNull_throwsNullException() {
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            credentialsService.markAsCancelled(null);
        });

        assertEquals(nullPointerException.getMessage(), "apiKey is marked non-null but is null");
    }
}