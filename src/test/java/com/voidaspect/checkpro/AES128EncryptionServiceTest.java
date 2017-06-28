package com.voidaspect.checkpro;

import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.fail;
import static org.quicktheories.quicktheories.QuickTheory.qt;
import static org.quicktheories.quicktheories.generators.SourceDSL.strings;

/**
 * @author miwag.
 */
public class AES128EncryptionServiceTest {

    private AES128EncryptionService encryptionService;

    @Before
    public void setUp() throws Exception {
        byte[] keyBytes = new byte[16];
        new Random().nextBytes(keyBytes);
        String key = new String(keyBytes, "UTF-8");
        System.out.println("key: " + key);
        encryptionService = new AES128EncryptionService(key);
    }

    @Test
    public void testEncryption() throws Exception {
        qt().withExamples(20000).forAll(strings().ascii()
                .ofLengthBetween(1, 1000)
                .describedAs(data -> "Input: " + data))
                .check(s -> s.equals(encryptDecrypt(s)));
    }

    private String encryptDecrypt(String s) {
        try {
            return encryptionService.decrypt(encryptionService.encrypt(s));
        } catch (Exception e) {
            fail(e.getMessage());
            throw new RuntimeException("UNREACHABLE", e);
        }
    }

}