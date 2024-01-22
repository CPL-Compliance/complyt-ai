package io.complyt.authentication.services;

import io.complyt.authentication.domain.ApiKey;
import io.complyt.authentication.domain.Token;
import io.complyt.authentication.repositories.TokenRepository;
import io.complyt.authentication.security.Crypto;
import io.complyt.authentication.security.EncryptedData;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Slf4j
@EqualsAndHashCode
public class TokenService {
    @NonNull
    TokenRepository tokenRepository;

    @NonNull
    PasswordEncoder passwordEncoder;

    @NonNull
    Crypto cryptoAesGcmNoPadding;

    int tokenExpirationSafeWindowSec;


    public Mono<Token> findByApiKeyAndDecrypt(final @NonNull ApiKey apiKey) {
        return findByApiKey(apiKey)
                .map(this::decryptToken);
    }

    public Mono<Token> findByApiKey(final @NonNull ApiKey apiKey) {
        return tokenRepository.findByComplytClientId(apiKey.clientId())
                .filter(token -> passwordEncoder.matches(apiKey.clientSecret(), token.getComplytClientSecret()));
    }

    public Mono<Token> saveToken(@NonNull Token token) {
        return Mono.just(createDocumentExpirationDateTime(token.getExpiresIn()))
                .map(token::withExpireAt)
                .map(this::encryptToken)
                .flatMap(tokenRepository::save)
                .map(this::decryptToken);
    }

    public Mono<Token> deleteToken(@NonNull ApiKey apiKey) {
        return tokenRepository.deleteByComplytClientId(apiKey.clientId());
    }

    @NonNull
    private LocalDateTime createDocumentExpirationDateTime(int expiresIn) {
        return LocalDateTime.now().plusSeconds(expiresIn).minusSeconds(tokenExpirationSafeWindowSec);
    }

    @NonNull
    private Token decryptToken(final Token token) {
        EncryptedData accessTokenEncryptedData = new EncryptedData(token.getAccessTokenIv(), token.getAccessToken());
        EncryptedData scopeEncryptedData = new EncryptedData(token.getScopeIv(), token.getScope());

        String scope;
        String accessToken;
        try {
            scope = cryptoAesGcmNoPadding.decrypt(scopeEncryptedData);
            accessToken = cryptoAesGcmNoPadding.decrypt(accessTokenEncryptedData);
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException |
                 InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to decrypt data");
        }

        return createDecryptedToken(token, accessToken, scope, scopeEncryptedData);
    }

    @NonNull
    private Token encryptToken(final Token token) {
        EncryptedData accessTokenEncryptedData;
        EncryptedData scopeEncryptedData;

        try {
            accessTokenEncryptedData = cryptoAesGcmNoPadding.encrypt(token.getAccessToken());
            scopeEncryptedData = cryptoAesGcmNoPadding.encrypt(token.getScope());
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                 InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }

        return createEncryptedToken(token, accessTokenEncryptedData, scopeEncryptedData);
    }

    private Token createDecryptedToken(Token token, String accessToken, String scope,
                                       EncryptedData scopeEncryptedData) {
        return Token.builder()
                .accessToken(accessToken)
                .accessTokenIv(token.getAccessTokenIv())
                .tokenType(token.getTokenType())
                .complytClientSecret(token.getComplytClientSecret())
                .complytClientId(token.getComplytClientId())
                .expiresIn(token.getExpiresIn())
                .scope(scope)
                .scopeIv(scopeEncryptedData.iv())
                .expireAt(token.getExpireAt())
                .createdAt(token.getCreatedAt())
                .id(token.getId())
                .build();
    }

    private Token createEncryptedToken(Token token, EncryptedData accessTokenEncryptedData,
                                       EncryptedData scopeEncryptedData) {
        return Token.builder()
                .accessToken(accessTokenEncryptedData.cipherText())
                .accessTokenIv(accessTokenEncryptedData.iv())
                .tokenType(token.getTokenType())
                .complytClientSecret(token.getComplytClientSecret())
                .complytClientId(token.getComplytClientId())
                .expiresIn(token.getExpiresIn())
                .scope(scopeEncryptedData.cipherText())
                .scopeIv(scopeEncryptedData.iv())
                .expireAt(token.getExpireAt())
                .createdAt(token.getCreatedAt())
                .id(token.getId())
                .build();
    }
}