package io.complyt.authentication.services;

import io.complyt.authentication.domain.Token;
import io.complyt.authentication.repositories.TokenRepository;
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
import testUtils.TestUtilities;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;
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
    Crypto cryptoAesCbcPkcs5Padding;

    @BeforeEach
    void setUp() {
        int tokenExpirationSafeWindowSec = 10;
        tokenService = new TokenService(tokenRepository, passwordEncoder, cryptoAesCbcPkcs5Padding,
                tokenExpirationSafeWindowSec);
    }

    @Test
    void saveToken() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Given
        Token token = TestUtilities.createToken();
        String accessTokenPlainText = "accessTokenPlainText";
        String scopePlainText = "scopePlainText";
        EncryptedData accessTokenEncryptedData = new EncryptedData("accessTokenIv",
                "accessTokenCipherText");
        EncryptedData scopeEncryptedData = new EncryptedData("scopeIv",
                "scopeCipherText");

        Token encryptedToken = TestUtilities.createEncryptedToken(token, accessTokenEncryptedData, scopeEncryptedData);

        // When
        when(cryptoAesCbcPkcs5Padding.encrypt(token.getAccessToken())).thenReturn(accessTokenEncryptedData);
        when(cryptoAesCbcPkcs5Padding.encrypt(token.getScope())).thenReturn(scopeEncryptedData);
        when(cryptoAesCbcPkcs5Padding.decrypt(accessTokenEncryptedData)).thenReturn(accessTokenPlainText);
        when(cryptoAesCbcPkcs5Padding.decrypt(scopeEncryptedData)).thenReturn(scopePlainText);
        when(tokenRepository.save(any())).thenReturn(Mono.just(encryptedToken));

        // Then
        Mono<Token> tokenMono = tokenService.saveToken(token);

        StepVerifier.create(tokenMono).consumeNextWith(tkn -> assertThat(tkn.getAccessToken().equals(encryptedToken.getAccessToken()))).verifyComplete();
    }

    @Test
    void findByApiKey() {
    }
}