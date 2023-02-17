package nms.security;

import no.ntnu.nms.security.KeyGenerator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class KeyGeneratorTest {

    @Test
    public void TestEqualKeysKEY() {
        assertArrayEquals(KeyGenerator.KEY, KeyGenerator.KEY); // KeyGenerator.KEY calls the generateKey() method.
    }

    @Test
    public void TestKeyLengthKEY() {
        byte[] key = KeyGenerator.KEY;
        assertEquals(32, key.length);
    }
}
