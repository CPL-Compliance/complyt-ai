package io.complyt.authentication.services;

import io.complyt.authentication.domain.Credentials;
import io.complyt.authentication.repositories.CredentialsRepository;
import io.complyt.authentication.security.Cryptor;
import io.complyt.authentication.security.EncryptedData;
import io.complyt.authentication.v1.models.ApiKey;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class CredentialsService {
    @NonNull
    CredentialsRepository credentialsRepository;

    @NonNull
    private PasswordEncoder passwordEncoder;

    @NonNull
    private Cryptor cryptorAesCbcPkcs5Padding;

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
            clientId = cryptorAesCbcPkcs5Padding.decrypt(encryptedClientId);
            clientSecret = cryptorAesCbcPkcs5Padding.decrypt(encryptedClientSecret);
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
            clientIdEncryptedData = cryptorAesCbcPkcs5Padding.encrypt(credentials.getClientId());
            clientSecretEncryptedData = cryptorAesCbcPkcs5Padding.encrypt(credentials.getClientSecret());
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
