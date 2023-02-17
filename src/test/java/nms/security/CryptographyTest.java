package nms.security;

import no.ntnu.nms.exception.CryptographyException;
import no.ntnu.nms.security.Cryptography;
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
    public void TestEncryption() {
        byte[] encryptedMessage = null;
        try {
            encryptedMessage = Cryptography.xorWithKey(message.getBytes(),key);
        } catch (Exception ignore) {
            fail();
        }

        assertArrayEquals(new byte[]{69, 87, 64, 64}, encryptedMessage);
    }

    @Test
    public void TestDecryption(){
        byte[] decryptedMessage = null;
        try {
            decryptedMessage = Cryptography.xorWithKey(new byte[]{69, 87, 64, 64}, key);
        } catch (Exception e) {
            fail();
        }

        assertArrayEquals(message.getBytes(), decryptedMessage);
    }

    @Test
    public void TestXorWithKeyNegative(){
        assertThrows(CryptographyException.class, () -> Cryptography.xorWithKey(null, null));
    }
}
