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

        // Generate IV (Initialization Vector)
        byte[] iv = new byte[12];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);

        // Create GCMParameterSpec
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, iv);

        // Create Cipher instance for AES/GCM/NoPadding
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);

        // Encrypt the data
        byte[] cipherText = cipher.doFinal(plainText.getBytes());
        String cipherTextStr = Base64.getEncoder().encodeToString(cipherText);

        // Encode the result in Base64
        return new EncryptedData(Base64.getEncoder().encodeToString(iv), cipherTextStr);
    }

    public @NonNull String decrypt(final @NonNull EncryptedData encryptedData) throws IllegalBlockSizeException,
            BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException,
            NoSuchAlgorithmException {

        // Decode the Base64 encoded input

        // Create GCMParameterSpec
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, Base64.getDecoder().decode(encryptedData.iv()));

        // Create Cipher instance for AES/GCM/NoPadding
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

        // Decrypt the data
        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(encryptedData.cipherText()));

        // Convert decrypted bytes to string
        return new String(plainText);
    }

//    public @NonNull EncryptedData encrypt(final @NonNull String plainText) throws InvalidAlgorithmParameterException,
//            InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException,
//            NoSuchAlgorithmException {
//        IvParameterSpec ivParameterSpec = generateIv();
//
//        Cipher cipher = Cipher.getInstance(algorithm);
//        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
//        byte[] cipherText = cipher.doFinal(plainText.getBytes());
//        String cipherTextStr = Base64.getEncoder().encodeToString(cipherText);
//
//        return new EncryptedData(Base64.getEncoder().encodeToString(ivParameterSpec.getIV()), cipherTextStr);
//    }
//
//    public @NonNull String decrypt(final @NonNull EncryptedData encryptedData) throws IllegalBlockSizeException,
//            BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException,
//            NoSuchPaddingException, NoSuchAlgorithmException {
//        IvParameterSpec ivParameterSpec = new IvParameterSpec(Base64.getDecoder().decode(encryptedData.iv()));
//
//        Cipher cipher = Cipher.getInstance(algorithm);
//        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
//
//        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(encryptedData.cipherText()));
//
//        return new String(plainText);
//    }
//
//    private IvParameterSpec generateIv() {
//        byte[] iv = new byte[16];
//        (new SecureRandom()).nextBytes(iv);
//
//        return new IvParameterSpec(iv);
//    }
}
