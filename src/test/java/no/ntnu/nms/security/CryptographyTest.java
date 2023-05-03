package no.ntnu.nms.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CryptographyTest {

    private String message;
    @BeforeEach
    public void setUp(){
        message = "test";
    }

    @Test
    public void TestApplyCipher() {
        byte[] encryptedMessage = null;
        try {
            encryptedMessage = Cryptography.applyCipher(message.getBytes(), Cryptography.Mode.ENCRYPT);
        } catch (Exception ignore) {
            fail();
        }

        byte[] decryptedMessage = null;
        try {
            decryptedMessage = Cryptography.applyCipher(encryptedMessage, Cryptography.Mode.DECRYPT);
        } catch (Exception ignore) {
            fail();
        }

        assertArrayEquals(message.getBytes(), decryptedMessage);
    }
}
