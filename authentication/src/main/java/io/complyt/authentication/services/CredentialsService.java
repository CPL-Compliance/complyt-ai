package io.complyt.authentication.services;

import io.complyt.authentication.domain.Credentials;
import io.complyt.authentication.repositories.CredentialsRepository;
import io.complyt.authentication.security.Crypto;
import io.complyt.authentication.security.EncryptedData;
import io.complyt.authentication.v1.models.ApiKey;
import lombok.AccessLevel;
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
    String issuerUri;

    public Mono<Credentials> getCredentialsByApiKey(final @NonNull ApiKey apiKey) {
        return credentialsRepository.findByComplytClientId(apiKey.getClientId())
                .filter(credentials -> passwordEncoder.matches(apiKey.getClientSecret(), credentials.getComplytClientSecret()))
                .flatMap(this::decrypt);
    }

    private Mono<Credentials> decrypt(Credentials credentials) {
        EncryptedData encryptedClientId = new EncryptedData(credentials.getClientIdIv(), credentials.getClientId());
        EncryptedData encryptedClientSecret = new EncryptedData(credentials.getClientSecretIv(), credentials.getClientSecret());

        String clientId;
        String clientSecret;
        try {
            clientId = cryptoAesCbcPkcs5Padding.decrypt(encryptedClientId);
            clientSecret = cryptoAesCbcPkcs5Padding.decrypt(encryptedClientSecret);
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException |
                 InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to decrypt credentials.");
        }

        Credentials decryptedCreds = credentials.withClientId(clientId).withClientSecret(clientSecret);

        return Mono.just(decryptedCreds);
    }

    public Mono<Credentials> saveCredentials(Credentials credentials, ApiKey apiKey) {
        EncryptedData clientIdEncryptedData;
        EncryptedData clientSecretEncryptedData;

        try {
            clientIdEncryptedData = cryptoAesCbcPkcs5Padding.encrypt(credentials.getClientId());
            clientSecretEncryptedData = cryptoAesCbcPkcs5Padding.encrypt(credentials.getClientSecret());
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                 InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }

        String clientSecret = apiKey.getClientSecret();
        String clientSecretEncoded = passwordEncoder.encode(clientSecret);
        Credentials credentialsToSave = Credentials.builder()
                .clientId(clientIdEncryptedData.cipherText())
                .clientIdIv(clientIdEncryptedData.iv())
                .clientSecret(clientSecretEncryptedData.cipherText())
                .clientSecretIv(clientSecretEncryptedData.iv())
                .audience("https://sales-tax-service/")
                .grantType("client_credentials")
                .complytClientId(apiKey.getClientId())
                .complytClientSecret(clientSecretEncoded)
                .build();

        return credentialsRepository.save(credentialsToSave);
    }
}
