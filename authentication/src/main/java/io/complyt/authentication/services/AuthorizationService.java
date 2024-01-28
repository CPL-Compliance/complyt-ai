package io.complyt.authentication.services;

import io.complyt.authentication.business.authorization.*;
import io.complyt.authentication.domain.Credentials;
import io.complyt.authentication.domain.Token;
import io.complyt.authentication.security.Crypto;
import io.complyt.authentication.security.EncryptedData;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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

    public Mono<Auth0Client> deleteApiKey(@NonNull Credentials credentials, @NonNull String accessToken) {

        String decodedClientId;
        EncryptedData encryptedClientId = new EncryptedData(credentials.getClientIdIv(), credentials.getClientId());

        try {
            decodedClientId = cryptoAesGcmNoPadding.decrypt(encryptedClientId);
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException |
                 InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to decrypt credentials.");
        }
        return authorizationServerWrapper.removeApiKeyFromClient(credentials.getName(), decodedClientId, credentials.getTenantId(), accessToken, null, null);
    }


    public Mono<TenentIdAndNameObject> getTenantIdAndClientName(@NonNull Credentials credentials) {
        return authorizationServerWrapper
                .getManagementAccessToken()
                .flatMap(token -> authorizationServerWrapper.getTenantIdAndClientNameFromAuth0(credentials.getClientId(), token.accessToken()))
                .flatMap(auth0Client -> Mono.just(new TenentIdAndNameObject(auth0Client.getClient_metadata().getTenant_id(), auth0Client.getName())));
    }

    public Mono<String> getMangementAccessToken() {
        return authorizationServerWrapper
                .getManagementAccessToken()
                .map(AccessToken::accessToken);
    }


}
