package io.complyt.authentication.security;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CryptoAesGcmNoPadding implements Crypto {
    String algorithm = "AES/GCM/NoPadding";

    @NonNull
    SecretKey secretKey;

    public @NonNull EncryptedData encrypt(final @NonNull String plainText) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        byte[] iv = new byte[12];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);

        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, iv);

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);

        byte[] cipherText = cipher.doFinal(plainText.getBytes());
        String cipherTextStr = Base64.getEncoder().encodeToString(cipherText);

        return new EncryptedData(Base64.getEncoder().encodeToString(iv), cipherTextStr);
    }

    public @NonNull String decrypt(final @NonNull EncryptedData encryptedData) throws IllegalBlockSizeException,
            BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException,
            NoSuchAlgorithmException {

        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, Base64.getDecoder().decode(encryptedData.iv()));

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(encryptedData.cipherText()));

        return new String(plainText);
    }

}