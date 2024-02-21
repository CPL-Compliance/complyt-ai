package io.complyt.authentication.services;

import io.complyt.authentication.domain.ApiKey;
import io.complyt.authentication.domain.Token;
import io.complyt.authentication.repositories.TokenRepository;
import io.complyt.authentication.security.Crypto;
import io.complyt.authentication.security.EncryptedData;
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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
class TokenServiceTest {
    TokenService tokenService;

    @Mock
    TokenRepository tokenRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    Crypto cryptoAesGcmNoPadding;

    final int tokenExpirationSafeWindowSec = 10;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService(tokenRepository, passwordEncoder, cryptoAesGcmNoPadding,
                tokenExpirationSafeWindowSec);
    }

    @Test
    void saveToken_validToken_returnsSavedToken() throws InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Given
        Token token = TestUtilities.createToken();
        String accessTokenPlainText = "accessTokenPlainText";
        String scopePlainText = "scopePlainText";
        EncryptedData accessTokenEncryptedData = new EncryptedData("accessTokenIv",
                "accessTokenCipherText");
        EncryptedData scopeEncryptedData = new EncryptedData("scopeIv", "scopeCipherText");

        Token encryptedToken = TestUtilities.createEncryptedToken(token, accessTokenEncryptedData, scopeEncryptedData);
        encryptedToken = encryptedToken.withExpireAt(createDocumentExpirationDateTime(encryptedToken.getExpiresIn()));

        // When
        when(cryptoAesGcmNoPadding.encrypt(token.getAccessToken())).thenReturn(accessTokenEncryptedData);
        when(cryptoAesGcmNoPadding.encrypt(token.getScope())).thenReturn(scopeEncryptedData);
        when(cryptoAesGcmNoPadding.decrypt(accessTokenEncryptedData)).thenReturn(accessTokenPlainText);
        when(cryptoAesGcmNoPadding.decrypt(scopeEncryptedData)).thenReturn(scopePlainText);
        when(tokenRepository.save(any())).thenReturn(Mono.just(encryptedToken));

        // Then
        Mono<Token> tokenMono = tokenService.saveToken(token);

        Token finalEncryptedToken = encryptedToken;
        StepVerifier.create(tokenMono).consumeNextWith(tkn ->
                assertThat(tkn.getAccessToken().equals(finalEncryptedToken.getAccessToken()) &&
                        tkn.getAccessTokenIv().equals(finalEncryptedToken.getAccessTokenIv()) &&
                        tkn.getTokenType().equals(finalEncryptedToken.getTokenType()) &&
                        tkn.getId().equals(finalEncryptedToken.getId()) &&
                        tkn.getScope().equals(finalEncryptedToken.getScope()) &&
                        tkn.getComplytClientId().equals(finalEncryptedToken.getComplytClientId()) &&
                        tkn.getComplytClientSecret().equals(finalEncryptedToken.getComplytClientSecret()) &&
                        tkn.getExpiresIn() == finalEncryptedToken.getExpiresIn() &&
                        tkn.getCreatedAt().isBefore(finalEncryptedToken.getCreatedAt()) &&
                        tkn.getExpireAt().isBefore(finalEncryptedToken.getExpireAt()))).verifyComplete();
    }

    private @NonNull LocalDateTime createDocumentExpirationDateTime(int expiresIn) {
        return LocalDateTime.now().plusSeconds(expiresIn).minusSeconds(tokenExpirationSafeWindowSec);
    }

    @Test
    void saveToken_validToken_expiredAtSmallesThenExpiresInMinus10Secs() throws InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException,
            InvalidKeyException {
        // Given
        Token token = TestUtilities.createToken();
        String accessTokenPlainText = "accessTokenPlainText";
        String scopePlainText = "scopePlainText";
        EncryptedData accessTokenEncryptedData = new EncryptedData("accessTokenIv",
                "accessTokenCipherText");
        EncryptedData scopeEncryptedData = new EncryptedData("scopeIv",
                "scopeCipherText");

        // When
        when(cryptoAesGcmNoPadding.encrypt(token.getAccessToken())).thenReturn(accessTokenEncryptedData);
        when(cryptoAesGcmNoPadding.encrypt(token.getScope())).thenReturn(scopeEncryptedData);
        when(cryptoAesGcmNoPadding.decrypt(accessTokenEncryptedData)).thenReturn(accessTokenPlainText);
        when(cryptoAesGcmNoPadding.decrypt(scopeEncryptedData)).thenReturn(scopePlainText);
        when(tokenRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // Then
        Mono<Token> tokenMono = tokenService.saveToken(token);

        StepVerifier.create(tokenMono).consumeNextWith(tkn ->
                assertThat(ChronoUnit.SECONDS.between(LocalDateTime.now(), tkn.getExpireAt()) >=
                        TestUtilities.expiresIn - tokenExpirationSafeWindowSec)).verifyComplete();
    }

    @Test
    void saveToken_encryptionThrowsNoSuchAlgorithmException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Given
        Token token = TestUtilities.createToken();

        // When
        when(cryptoAesGcmNoPadding.encrypt(token.getAccessToken()))
                .thenThrow(new NoSuchAlgorithmException("Error"));

        // Then
        Mono<Token> tokenMono = tokenService.saveToken(token);

        StepVerifier.create(tokenMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void saveToken_encryptionThrowsBadPaddingException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Given
        Token token = TestUtilities.createToken();

        // When
        when(cryptoAesGcmNoPadding.encrypt(token.getAccessToken())).thenThrow(new BadPaddingException("Error"));

        // Then
        Mono<Token> tokenMono = tokenService.saveToken(token);

        StepVerifier.create(tokenMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void saveToken_encryptionThrowsIllegalBlockSizeException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Given
        Token token = TestUtilities.createToken();

        // When
        when(cryptoAesGcmNoPadding.encrypt(token.getAccessToken()))
                .thenThrow(new IllegalBlockSizeException("Error"));

        // Then
        Mono<Token> tokenMono = tokenService.saveToken(token);

        StepVerifier.create(tokenMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void saveToken_encryptionThrowsInvalidAlgorithmParameterException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Given
        Token token = TestUtilities.createToken();

        // When
        when(cryptoAesGcmNoPadding.encrypt(token.getAccessToken()))
                .thenThrow(new InvalidAlgorithmParameterException("Error"));

        // Then
        Mono<Token> tokenMono = tokenService.saveToken(token);

        StepVerifier.create(tokenMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void saveToken_encryptionThrowsInvalidKeyException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Given
        Token token = TestUtilities.createToken();

        // When
        when(cryptoAesGcmNoPadding.encrypt(token.getAccessToken())).thenThrow(new InvalidKeyException("Error"));

        // Then
        Mono<Token> tokenMono = tokenService.saveToken(token);

        StepVerifier.create(tokenMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void saveToken_encryptionThrowsNoSuchPaddingException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Given
        Token token = TestUtilities.createToken();

        // When
        when(cryptoAesGcmNoPadding.encrypt(token.getAccessToken())).thenThrow(new NoSuchPaddingException("Error"));

        // Then
        Mono<Token> tokenMono = tokenService.saveToken(token);

        StepVerifier.create(tokenMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void saveToken_decryptionThrowsNoSuchAlgorithmException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Given
        Token token = TestUtilities.createToken();
        EncryptedData accessTokenEncryptedData = new EncryptedData("accessTokenIv",
                "accessTokenCipherText");
        EncryptedData scopeEncryptedData = new EncryptedData("scopeIv", "scopeCipherText");

        // When
        when(cryptoAesGcmNoPadding.encrypt(token.getAccessToken())).thenReturn(accessTokenEncryptedData);
        when(cryptoAesGcmNoPadding.encrypt(token.getScope())).thenReturn(scopeEncryptedData);
        when(tokenRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(cryptoAesGcmNoPadding.decrypt(scopeEncryptedData)).thenThrow(new NoSuchAlgorithmException("Error"));

        // Then
        Mono<Token> tokenMono = tokenService.saveToken(token);

        StepVerifier.create(tokenMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void saveToken_decryptionThrowsBadPaddingException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Given
        Token token = TestUtilities.createToken();
        EncryptedData accessTokenEncryptedData = new EncryptedData("accessTokenIv",
                "accessTokenCipherText");
        EncryptedData scopeEncryptedData = new EncryptedData("scopeIv", "scopeCipherText");

        // When
        when(cryptoAesGcmNoPadding.encrypt(token.getAccessToken())).thenReturn(accessTokenEncryptedData);
        when(cryptoAesGcmNoPadding.encrypt(token.getScope())).thenReturn(scopeEncryptedData);
        when(tokenRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(cryptoAesGcmNoPadding.decrypt(scopeEncryptedData)).thenThrow(new BadPaddingException("Error"));

        // Then
        Mono<Token> tokenMono = tokenService.saveToken(token);

        StepVerifier.create(tokenMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void saveToken_decryptionThrowsIllegalBlockSizeException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Given
        Token token = TestUtilities.createToken();
        EncryptedData accessTokenEncryptedData = new EncryptedData("accessTokenIv",
                "accessTokenCipherText");
        EncryptedData scopeEncryptedData = new EncryptedData("scopeIv", "scopeCipherText");

        // When
        when(cryptoAesGcmNoPadding.encrypt(token.getAccessToken())).thenReturn(accessTokenEncryptedData);
        when(cryptoAesGcmNoPadding.encrypt(token.getScope())).thenReturn(scopeEncryptedData);
        when(tokenRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(cryptoAesGcmNoPadding.decrypt(scopeEncryptedData)).thenThrow(new IllegalBlockSizeException("Error"));

        // Then
        Mono<Token> tokenMono = tokenService.saveToken(token);

        StepVerifier.create(tokenMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void saveToken_decryptionThrowsInvalidAlgorithmParameterException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Given
        Token token = TestUtilities.createToken();
        EncryptedData accessTokenEncryptedData = new EncryptedData("accessTokenIv",
                "accessTokenCipherText");
        EncryptedData scopeEncryptedData = new EncryptedData("scopeIv", "scopeCipherText");

        // When
        when(cryptoAesGcmNoPadding.encrypt(token.getAccessToken())).thenReturn(accessTokenEncryptedData);
        when(cryptoAesGcmNoPadding.encrypt(token.getScope())).thenReturn(scopeEncryptedData);
        when(tokenRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(cryptoAesGcmNoPadding.decrypt(scopeEncryptedData))
                .thenThrow(new InvalidAlgorithmParameterException("Error"));

        // Then
        Mono<Token> tokenMono = tokenService.saveToken(token);

        StepVerifier.create(tokenMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void saveToken_decryptionThrowsInvalidKeyException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Given
        Token token = TestUtilities.createToken();
        EncryptedData accessTokenEncryptedData = new EncryptedData("accessTokenIv",
                "accessTokenCipherText");
        EncryptedData scopeEncryptedData = new EncryptedData("scopeIv", "scopeCipherText");

        // When
        when(cryptoAesGcmNoPadding.encrypt(token.getAccessToken())).thenReturn(accessTokenEncryptedData);
        when(cryptoAesGcmNoPadding.encrypt(token.getScope())).thenReturn(scopeEncryptedData);
        when(tokenRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(cryptoAesGcmNoPadding.decrypt(scopeEncryptedData)).thenThrow(new InvalidKeyException("Error"));

        // Then
        Mono<Token> tokenMono = tokenService.saveToken(token);

        StepVerifier.create(tokenMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void saveToken_decryptionThrowsNoSuchPaddingException_throwsRuntimeException()
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Given
        Token token = TestUtilities.createToken();
        EncryptedData accessTokenEncryptedData = new EncryptedData("accessTokenIv",
                "accessTokenCipherText");
        EncryptedData scopeEncryptedData = new EncryptedData("scopeIv", "scopeCipherText");

        // When
        when(cryptoAesGcmNoPadding.encrypt(token.getAccessToken())).thenReturn(accessTokenEncryptedData);
        when(cryptoAesGcmNoPadding.encrypt(token.getScope())).thenReturn(scopeEncryptedData);
        when(tokenRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(cryptoAesGcmNoPadding.decrypt(scopeEncryptedData)).thenThrow(new NoSuchPaddingException("Error"));

        // Then
        Mono<Token> tokenMono = tokenService.saveToken(token);

        StepVerifier.create(tokenMono).expectError(RuntimeException.class).verify();
    }

    @Test
    void findByApiKeyAndDecrypt_apiKeyExists_returnsMonoToken() throws InvalidAlgorithmParameterException,
            IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException,
            InvalidKeyException {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Token token = TestUtilities.createToken();

        token = token.withAccessToken("accessTokenPlainText").withScope("scopePlainText")
                .withAccessTokenIv("accessTokenIv").withScopeIv("scopeIv");

        EncryptedData accessTokenEncryptedData = new EncryptedData("accessTokenIv",
                "accessTokenCipherText");

        EncryptedData scopeEncryptedData = new EncryptedData("scopeIv", "scopeCipherText");
        String accessTokenPlainText = "accessTokenPlainText";
        String scopePlainText = "scopePlainText";

        Token encryptedToken = TestUtilities.createEncryptedToken(token, accessTokenEncryptedData, scopeEncryptedData);

        // When
        when(tokenRepository.findByComplytClientId(apiKey.clientId())).thenReturn(Mono.just(encryptedToken));
        when(passwordEncoder.matches(apiKey.clientSecret(), encryptedToken.getComplytClientSecret()))
                .thenReturn(true);

        when(cryptoAesGcmNoPadding.decrypt(accessTokenEncryptedData)).thenReturn(accessTokenPlainText);
        when(cryptoAesGcmNoPadding.decrypt(scopeEncryptedData)).thenReturn(scopePlainText);

        // Then
        Mono<Token> tokenMono = tokenService.findByApiKeyAndDecrypt(apiKey);

        StepVerifier.create(tokenMono).expectNext(token).verifyComplete();
    }

    @Test
    void findByApiKeyAndDecrypt_apiKeyDoesntExist_returnsMonoEmpty() {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();

        // When
        when(tokenRepository.findByComplytClientId(apiKey.clientId())).thenReturn(Mono.empty());

        // Then
        Mono<Token> tokenMono = tokenService.findByApiKeyAndDecrypt(apiKey);

        StepVerifier.create(tokenMono).verifyComplete();
    }

    @Test
    void findByApiKeyAndDecrypt_authenticationFails_returnsMonoEmpty() {
        // Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Token token = TestUtilities.createToken();

        token = token.withAccessToken("accessTokenPlainText").withScope("scopePlainText")
                .withAccessTokenIv("accessTokenIv").withScopeIv("scopeIv");

        EncryptedData accessTokenEncryptedData = new EncryptedData("accessTokenIv",
                "accessTokenCipherText");

        EncryptedData scopeEncryptedData = new EncryptedData("scopeIv", "scopeCipherText");

        Token encryptedToken = TestUtilities.createEncryptedToken(token, accessTokenEncryptedData, scopeEncryptedData);

        // When
        when(tokenRepository.findByComplytClientId(apiKey.clientId())).thenReturn(Mono.just(encryptedToken));
        when(passwordEncoder.matches(apiKey.clientSecret(), encryptedToken.getComplytClientSecret()))
                .thenReturn(false);

        // Then
        Mono<Token> tokenMono = tokenService.findByApiKeyAndDecrypt(apiKey);

        StepVerifier.create(tokenMono).verifyComplete();
    }

    @Test
    void deleteToken_tokenFound_returnsDeletedToken(){
        //Given
        ApiKey apiKey = TestUtilities.createApiKey();
        Token token = TestUtilities.createToken();

        //When
        when(tokenRepository.deleteByComplytClientId(apiKey.clientId())).thenReturn(Mono.just(token));

        //Then
        Mono<Token> tokenMono = tokenService.deleteToken(apiKey);
        StepVerifier.create(tokenMono).expectNext(token).verifyComplete();
    }

    @Test
    void deleteToken_tokenNotFound_returnsMonoEmpty(){
        //Given
        ApiKey apiKey = TestUtilities.createApiKey();

        //When
        when(tokenRepository.deleteByComplytClientId(apiKey.clientId())).thenReturn(Mono.empty());

        //Then
        Mono<Token> tokenMono = tokenService.deleteToken(apiKey);
        StepVerifier.create(tokenMono).verifyComplete();
    }

    @Test
    void findByApiKeyAndDecrypt_apiKeyIsNull_throwsNullException() {
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            tokenService.findByApiKeyAndDecrypt(null);
        });

        assertEquals(nullPointerException.getMessage(), "apiKey is marked non-null but is null");
    }

    @Test
    void saveToken_tokenIsNull_throwsNullException() {
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            tokenService.saveToken(null);
        });

        assertEquals(nullPointerException.getMessage(), "token is marked non-null but is null");
    }

    @Test
    void deleteToken_tokenIsNull_throwsNullException() {
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            tokenService.deleteToken(null);
        });

        assertEquals(nullPointerException.getMessage(), "apiKey is marked non-null but is null");
    }
}