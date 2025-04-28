package io.complyt.authentication.services;

import io.complyt.authentication.domain.ApiKey;
import io.complyt.authentication.domain.Token;
import io.complyt.authentication.domain.enums.TokenSource;
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
        return tokenRepository.findByComplytClientId(apiKey.clientId())
                .filter(token -> passwordEncoder.matches(apiKey.clientSecret(), token.getComplytClientSecret()))
                .map(this::decryptToken);
    }

    // In the ApiKey the secret is already decrypted because we fetched it from credentials collection
    // Thus we do not need to use passwordEncoder.matches()
    public Mono<Token> findByApiKeyAndTenantIdForPartnerAndDecrypt(final @NonNull ApiKey apiKey, String tenantId) {
        return tokenRepository.findByComplytClientIdAndTenantId(apiKey.clientId(), tenantId)
                .filter(token -> apiKey.clientSecret().equals(token.getComplytClientSecret()))
                .map(this::decryptToken);
    }

    public Mono<Token> saveToken(@NonNull Token token, String clientTenantId) {
        return Mono.just(createDocumentExpirationDateTime(token.getExpiresIn()))
                .map(token::withExpireAt)
                .map(tokenWithDate -> tokenWithDate.withTokenSource(TokenSource.CLIENT))
                .map(this::encryptToken)
                .map(encryptedToken -> encryptedToken.withClientTenantId(clientTenantId))
                .flatMap(tokenRepository::save)
                .map(this::decryptToken);
    }

    // In use only for partnership
    // Function had tenantId argument - only for partner issuing a token of behalf of its client
    public Mono<Token> saveToken(@NonNull Token token, String clientTenantId, String partnerTenantId) {
        return Mono.just(createDocumentExpirationDateTime(token.getExpiresIn()))
                .map(token::withExpireAt)
                .map(tokenWithDate -> tokenWithDate.withTokenSource(TokenSource.PARTNER))
                .map(this::encryptToken)
                .map(encryptedToken -> encryptedToken.withClientTenantId(clientTenantId).withPartnerTenantId(partnerTenantId))
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
                .tokenSource(token.getTokenSource())
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
                .tokenSource(token.getTokenSource())
                .build();
    }
}