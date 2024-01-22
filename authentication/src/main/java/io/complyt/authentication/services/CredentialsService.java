package io.complyt.authentication.services;

import io.complyt.authentication.domain.ApiKey;
import io.complyt.authentication.domain.Credentials;
import io.complyt.authentication.domain.enums.ApiKeyStatus;
import io.complyt.authentication.repositories.CredentialsRepository;
import io.complyt.authentication.security.Crypto;
import io.complyt.authentication.security.EncryptedData;
import io.complyt.authentication.v1.exceptions.types.ApiKeyNotValidException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
@EqualsAndHashCode
public class CredentialsService {
    @NonNull
    CredentialsRepository credentialsRepository;

    @NonNull
    PasswordEncoder passwordEncoder;

    @NonNull
    Crypto cryptoAesGcmNoPadding;

    @NonNull
    String grantType;

    @NonNull
    String audience;

    public Mono<Credentials> getCredentialsByApiKeyAndDecrypt(final @NonNull ApiKey apiKey) {
        return getCredentialsByApiKey(apiKey)
                .filter(credentials -> credentials.getStatus().equals(ApiKeyStatus.ACTIVE))
                .switchIfEmpty(Mono.empty())
                .flatMap(this::decrypt);
    }

    public Mono<Credentials> getCredentialsByApiKey(final @NonNull ApiKey apiKey) {
        return credentialsRepository.findByComplytClientId(apiKey.clientId())
                .filter(credentials -> passwordEncoder.matches(apiKey.clientSecret(),
                        credentials.getComplytClientSecret()))
                .switchIfEmpty(Mono.error(new ApiKeyNotValidException()));
    }

    public Mono<Credentials> markAsCancelled(final @NonNull ApiKey apiKey) {
        return credentialsRepository.markAsCancelled(apiKey.clientId());
    }


    public Mono<Credentials> saveCredentials(@NonNull Credentials credentials, @NonNull ApiKey apiKey) {
        return Mono.fromSupplier(() -> prepareCredentialsForSave(credentials, apiKey))
                .flatMap(credentialsRepository::save);
    }

    private Credentials prepareCredentialsForSave(Credentials credentials, ApiKey apiKey) {
        EncryptedData clientIdEncryptedData;
        EncryptedData clientSecretEncryptedData;

        try {
            clientIdEncryptedData = cryptoAesGcmNoPadding.encrypt(credentials.getClientId());
            clientSecretEncryptedData = cryptoAesGcmNoPadding.encrypt(credentials.getClientSecret());
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                 InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException("Failed to encrypt credentials.");
        }

        String clientSecret = apiKey.clientSecret();
        String clientSecretEncoded = passwordEncoder.encode(clientSecret);

        Credentials encryptedCredentials = createEncryptedCredentials(apiKey, clientIdEncryptedData, clientSecretEncryptedData,
                clientSecretEncoded);

        return encryptedCredentials.withStatus(ApiKeyStatus.ACTIVE);
    }

    private @NonNull Mono<Credentials> decrypt(Credentials credentials) {
        EncryptedData encryptedClientId = new EncryptedData(credentials.getClientIdIv(), credentials.getClientId());
        EncryptedData encryptedClientSecret = new EncryptedData(credentials.getClientSecretIv(),
                credentials.getClientSecret());

        String clientId;
        String clientSecret;

        try {
            clientId = cryptoAesGcmNoPadding.decrypt(encryptedClientId);
            clientSecret = cryptoAesGcmNoPadding.decrypt(encryptedClientSecret);
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException |
                 InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to decrypt credentials.");
        }

        Credentials decryptedCreds = createDecryptedCredentials(credentials, clientId, clientSecret);

        return Mono.just(decryptedCreds);
    }

    private static Credentials createDecryptedCredentials(Credentials credentials, String clientId,
                                                          String clientSecret) {
        return Credentials.builder().clientId(clientId).clientSecret(clientSecret).audience(credentials.getAudience())
                .grantType(credentials.getGrantType()).complytClientId(credentials.getComplytClientId())
                .complytClientSecret(credentials.getComplytClientSecret()).build();
    }

    private Credentials createEncryptedCredentials(ApiKey apiKey, EncryptedData clientIdEncryptedData,
                                                   EncryptedData clientSecretEncryptedData,
                                                   String clientSecretEncoded) {
        return Credentials.builder().clientId(clientIdEncryptedData.cipherText())
                .clientIdIv(clientIdEncryptedData.iv())
                .clientSecret(clientSecretEncryptedData.cipherText())
                .clientSecretIv(clientSecretEncryptedData.iv()).audience(audience).grantType(grantType)
                .complytClientId(apiKey.clientId())
                .complytClientSecret(clientSecretEncoded).build();
    }
}
