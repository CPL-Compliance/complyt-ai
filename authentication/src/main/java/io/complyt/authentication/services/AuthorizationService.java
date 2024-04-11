package io.complyt.authentication.services;

import io.complyt.authentication.business.authorization.*;
import io.complyt.authentication.auth0_client.Auth0Client;
import io.complyt.authentication.domain.ApiKey;
import io.complyt.authentication.domain.Credentials;
import io.complyt.authentication.domain.TenantIdAndNameObject;
import io.complyt.authentication.domain.Token;
import io.complyt.authentication.security.Crypto;
import io.complyt.authentication.security.EncryptedData;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@EqualsAndHashCode
public class AuthorizationService {
    @NonNull
    AuthorizationServerWrapper authorizationServerWrapper;

    @NonNull
    Crypto cryptoAesGcmNoPadding;

    public Mono<Token> getToken(@NonNull Credentials credentials) {
        return authorizationServerWrapper.getAccessToken(credentials.getClientId(), credentials.getClientSecret(),
                        credentials.getAudience(), credentials.getGrantType())
                .mapNotNull(accessToken -> createToken(credentials, accessToken));
    }

    private Token createToken(Credentials credentials, AccessToken accessToken) {
        return Token.builder()
                .complytClientId(credentials.getComplytClientId())
                .complytClientSecret(credentials.getComplytClientSecret())
                .accessToken(accessToken.accessToken())
                .scope(accessToken.scope())
                .expiresIn(accessToken.expiresIn())
                .tokenType(accessToken.tokenType())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Mono<Auth0Client> deleteClientMetadata(@NonNull Credentials credentials, @NonNull String accessToken) {
        String decodedClientId = getDecryptClientId(credentials);
        return authorizationServerWrapper.updateApiKeyFromClient(credentials.getName(), decodedClientId, credentials.getTenantId(), accessToken, null, null);
    }

    public Mono<Auth0Client> rotateClientMetadata(@NonNull Credentials credentials, @NonNull ApiKey apiKey, @NonNull String accessToken) {
        String decodedClientId = getDecryptClientId(credentials);
        return authorizationServerWrapper.updateApiKeyFromClient(credentials.getName(), decodedClientId, credentials.getTenantId(), accessToken, apiKey.clientId(), apiKey.clientSecret());
    }

    private String getDecryptClientId(Credentials credentials) {
        EncryptedData encryptedClientId = new EncryptedData(credentials.getClientIdIv(), credentials.getClientId());
        try {
            return cryptoAesGcmNoPadding.decrypt(encryptedClientId);
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException |
                 InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to decrypt credentials.");
        }
    }

    public Mono<TenantIdAndNameObject> getTenantIdAndClientName(@NonNull Credentials credentials) {
        return authorizationServerWrapper
                .getManagementAccessToken()
                .flatMap(token -> authorizationServerWrapper.getTenantIdAndClientNameFromAuth0(credentials.getClientId(), token.accessToken()));
    }

    public Mono<String> getManagementAccessToken() {
        return authorizationServerWrapper
                .getManagementAccessToken()
                .map(AccessToken::accessToken);
    }


}
