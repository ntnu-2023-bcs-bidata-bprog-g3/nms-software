package no.ntnu.nms.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class KeyGeneratorTest {

    @Test
    public void TestEqualKeysKEY() {
        assertArrayEquals(Cryptography.KEY, Cryptography.KEY); // KeyGenerator.KEY calls the generateKey() method.
    }

    @Test
    public void TestKeyLengthKEY() {
        byte[] key = Cryptography.KEY;
        assertEquals(32, key.length);
    }
}
