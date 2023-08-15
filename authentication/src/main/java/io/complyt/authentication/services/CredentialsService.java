package io.complyt.authentication.services;

import io.complyt.authentication.domain.Credentials;
import io.complyt.authentication.repositories.CredentialsRepository;
import io.complyt.authentication.security.Crypto;
import io.complyt.authentication.security.EncryptedData;
import io.complyt.authentication.v1.models.ApiKey;
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
    Crypto cryptoAesCbcPkcs5Padding;

    @NonNull
    String grantType;

    @NonNull
    String audience;

    public Mono<Credentials> getCredentialsByApiKey(final @NonNull ApiKey apiKey) {
        return credentialsRepository.findByComplytClientId(apiKey.getClientId())
                .filter(credentials -> passwordEncoder.matches(apiKey.getClientSecret(),
                        credentials.getComplytClientSecret()))
                .switchIfEmpty(Mono.empty()).flatMap(this::decrypt);
    }

    public Mono<Credentials> saveCredentials(@NonNull Credentials credentials, ApiKey apiKey) {
        EncryptedData clientIdEncryptedData;
        EncryptedData clientSecretEncryptedData;

        try {
            clientIdEncryptedData = cryptoAesCbcPkcs5Padding.encrypt(credentials.getClientId());
            clientSecretEncryptedData = cryptoAesCbcPkcs5Padding.encrypt(credentials.getClientSecret());
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                 InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException("Failed to encrypt credentials.");
        }

        String clientSecret = apiKey.getClientSecret();
        String clientSecretEncoded = passwordEncoder.encode(clientSecret);
        Credentials credentialsToSave = createEncryptedCredentials(apiKey, clientIdEncryptedData,
                clientSecretEncryptedData, clientSecretEncoded);

        return credentialsRepository.save(credentialsToSave);
    }

    private @NonNull Mono<Credentials> decrypt(@NonNull Credentials credentials) {
        EncryptedData encryptedClientId = new EncryptedData(credentials.getClientIdIv(), credentials.getClientId());
        EncryptedData encryptedClientSecret = new EncryptedData(credentials.getClientSecretIv(),
                credentials.getClientSecret());

        String clientId;
        String clientSecret;
        try {
            clientId = cryptoAesCbcPkcs5Padding.decrypt(encryptedClientId);
            clientSecret = cryptoAesCbcPkcs5Padding.decrypt(encryptedClientSecret);
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException |
                 InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to decrypt credentials.");
        }

        Credentials decryptedCreds = createDecryptedCredentials(credentials, clientId, clientSecret);

        return Mono.just(decryptedCreds);
    }

    private static Credentials createDecryptedCredentials(@NonNull Credentials credentials, String clientId,
                                                          String clientSecret) {
        return Credentials.builder().clientId(clientId).clientSecret(clientSecret).audience(credentials.getAudience())
                .grantType(credentials.getGrantType()).complytClientId(credentials.getComplytClientId())
                .complytClientSecret(credentials.getComplytClientSecret()).build();
    }

    private Credentials createEncryptedCredentials(@NonNull ApiKey apiKey, @NonNull EncryptedData clientIdEncryptedData,
                                                   @NonNull EncryptedData clientSecretEncryptedData,
                                                   String clientSecretEncoded) {
        return Credentials.builder().clientId(clientIdEncryptedData.cipherText())
                .clientIdIv(clientIdEncryptedData.iv())
                .clientSecret(clientSecretEncryptedData.cipherText())
                .clientSecretIv(clientSecretEncryptedData.iv()).audience(audience).grantType(grantType)
                .complytClientId(apiKey.getClientId())
                .complytClientSecret(clientSecretEncoded).build();
    }
}
