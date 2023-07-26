package io.complyt.authentication.security;


import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CryptorAesCbcPkcs5Padding implements Cryptor {

    final String algorithm = "AES/CBC/PKCS5Padding";

    @NonNull
    SecretKey secretKey;

    public EncryptedData encrypt(final @NonNull String input) throws InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException {
        IvParameterSpec ivParameterSpec = generateIv();

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

        byte[] cipherText = cipher.doFinal(input.getBytes());

        String cipherTextStr = Base64.getEncoder().encodeToString(cipherText);

        return new EncryptedData(Base64.getEncoder().encodeToString(ivParameterSpec.getIV()), cipherTextStr);
    }

    public String decrypt(final @NonNull EncryptedData encryptedData) throws IllegalBlockSizeException,
            BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException,
            NoSuchPaddingException, NoSuchAlgorithmException {
        IvParameterSpec ivParameterSpec = new IvParameterSpec(Base64.getDecoder().decode(encryptedData.iv()));

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(encryptedData.cipherText()));

        return new String(plainText);
    }

    private IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);

        return new IvParameterSpec(iv);
    }
}
