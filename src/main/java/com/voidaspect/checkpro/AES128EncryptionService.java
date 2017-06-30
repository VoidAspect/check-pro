package com.voidaspect.checkpro;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

/**
 * @author miwag.
 */
public final class AES128EncryptionService {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final String ENCRYPTION_TYPE = "AES";

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";

    private static final int BYTES_IN_KEY = 128 / 8;

    private final byte[] key;

    public AES128EncryptionService(String key) {
        final byte[] keyBytes;
        keyBytes = key.getBytes(StandardCharsets.UTF_8);
        this.key = Arrays.copyOf(keyBytes, BYTES_IN_KEY);
    }

    public String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec secretKey = new SecretKeySpec(key, ENCRYPTION_TYPE);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] cipherText = cipher.doFinal(plainText.getBytes(CHARSET));
        return new String(Base64.getEncoder().encode(cipherText), CHARSET);
    }

    public String decrypt(String encryptedText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec secretKey = new SecretKeySpec(key, ENCRYPTION_TYPE);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] cipherText = Base64.getDecoder().decode(encryptedText.getBytes(CHARSET));
        return new String(cipher.doFinal(cipherText), CHARSET);
    }

}
