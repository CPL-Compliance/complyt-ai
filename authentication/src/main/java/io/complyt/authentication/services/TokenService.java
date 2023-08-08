package io.complyt.authentication.services;

import io.complyt.authentication.domain.Token;
import io.complyt.authentication.repositories.TokenRepository;
import io.complyt.authentication.security.Crypto;
import io.complyt.authentication.security.EncryptedData;
import io.complyt.authentication.v1.models.ApiKey;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
public class TokenService {
    @NonNull
    TokenRepository tokenRepository;

    @NonNull
    PasswordEncoder passwordEncoder;

    @NonNull
    Crypto cryptoAesCbcPkcs5Padding;

    int tokenExpirationSafeWindowSec;

    public Mono<Token> findByApiKey(final @NonNull ApiKey apiKey) {
        return tokenRepository.findByComplytClientId(apiKey.getClientId())
                .filter(token -> passwordEncoder.matches(apiKey.getClientSecret(), token.getComplytClientSecret()))
                .map(this::decryptToken);
    }

    public Mono<Token> saveToken(Token token) {
        return Mono.just(LocalDateTime.now().plusSeconds(token.getExpiresIn()).minusSeconds(tokenExpirationSafeWindowSec))
                .map(token::withExpireAt)
                .map(this::encryptToken)
                .flatMap(tokenRepository::save)
                .map(this::decryptToken);
    }

    @NonNull
    private Token decryptToken(@NonNull final Token token) {
        EncryptedData accessTokenEncryptedData = new EncryptedData(token.getAccessTokenIv(), token.getAccessToken());
        EncryptedData scopeEncryptedData = new EncryptedData(token.getScopeIv(), token.getScope());

        String scope;
        String accessToken;
        try {
            scope = cryptoAesCbcPkcs5Padding.decrypt(scopeEncryptedData);
            accessToken = cryptoAesCbcPkcs5Padding.decrypt(accessTokenEncryptedData);
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException |
                 InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

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
                .build();
    }

    @NonNull
    private Token encryptToken(@NonNull final Token token) {
        EncryptedData accessTokenEncryptedData;
        EncryptedData scopeEncryptedData;

        try {
            accessTokenEncryptedData = cryptoAesCbcPkcs5Padding.encrypt(token.getAccessToken());
            scopeEncryptedData = cryptoAesCbcPkcs5Padding.encrypt(token.getScope());
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                 InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }

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
                .build();
    }
}