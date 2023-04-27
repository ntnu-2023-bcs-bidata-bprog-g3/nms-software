package no.ntnu.nms.security;

import no.ntnu.nms.exception.CryptographyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class CryptographyTest {

    private String message;
    private byte[] key;

    @BeforeEach
    public void setUp(){
        message = "test";
        key = "1234".getBytes();
    }

    @Test
    public void TestXorWithKey() {
        byte[] encryptedMessage = null;
        try {
            encryptedMessage = Cryptography.xorWithKey(message.getBytes(),key);
        } catch (Exception ignore) {
            fail();
        }

        assertArrayEquals(new byte[]{69, 87, 64, 64}, encryptedMessage);

        byte[] decryptedMessage = null;
        try {
            decryptedMessage = Cryptography.xorWithKey(encryptedMessage, key);
        } catch (Exception ignore) {
            fail();
        }

        assertArrayEquals(message.getBytes(), decryptedMessage);
    }

    @Test
    public void TestXorWithKeyNegative(){
        assertThrows(CryptographyException.class, () -> Cryptography.xorWithKey(null, null));
    }
}
