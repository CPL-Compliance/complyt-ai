package io.complyt.authentication.services;

import io.complyt.authentication.domain.Credentials;
import io.complyt.authentication.domain.Token;
import io.complyt.authentication.repositories.CredentialsRepository;
import io.complyt.authentication.security.Cryptor;
import io.complyt.authentication.security.EncryptedData;
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

    public Mono<Credentials> getCredentialsByApiKey(final @NonNull Token token) {
        return Mono.just(passwordEncoder.encode(token.getApiKey()))
                .flatMap(credentialsRepository::findByApiKey)
                .flatMap(this::decrypt);
    }

    private Mono<Credentials> decrypt(Credentials credentials) {
        EncryptedData encryptedClientId = new EncryptedData(credentials.getIvClientId(), credentials.getClientId());
        EncryptedData encryptedClientSecret = new EncryptedData(credentials.getIvClientSecret(), credentials.getClientSecret());

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
}
