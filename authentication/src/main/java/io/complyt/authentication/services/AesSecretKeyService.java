package io.complyt.authentication.services;

import io.complyt.authentication.security.AesSecretKeyUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class AesSecretKeyService {

    public SecretKey generate256AesKey() {
        int keyLength = 256;
        return Objects.requireNonNull(AesSecretKeyUtils.generateAesKey(keyLength));
    }

    public String convertSecretKeyToString(SecretKey secretKey) {
        return AesSecretKeyUtils.convertSecretKeyToString(secretKey);
    }
}
