package com.crisismesh.crisismesh.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptionService {

    private static final String SECRET_KEY =
            "CrisisMeshKey123";

    public static String encrypt(String text)
            throws Exception {

        Cipher cipher =
                Cipher.getInstance("AES");

        SecretKeySpec key =
                new SecretKeySpec(
                        SECRET_KEY.getBytes(),
                        "AES"
                );

        cipher.init(
                Cipher.ENCRYPT_MODE,
                key
        );

        byte[] encrypted =
                cipher.doFinal(text.getBytes());

        return Base64.getEncoder()
                .encodeToString(encrypted);
    }

    public static String decrypt(String encryptedText)
            throws Exception {

        Cipher cipher =
                Cipher.getInstance("AES");

        SecretKeySpec key =
                new SecretKeySpec(
                        SECRET_KEY.getBytes(),
                        "AES"
                );

        cipher.init(
                Cipher.DECRYPT_MODE,
                key
        );

        byte[] decrypted =
                cipher.doFinal(
                        Base64.getDecoder()
                                .decode(encryptedText)
                );

        return new String(decrypted);
    }
}