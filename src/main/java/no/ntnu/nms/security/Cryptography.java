package no.ntnu.nms.security;

import org.springframework.util.SerializationUtils;

import no.ntnu.nms.domain_model.PoolRegistry;

/*
 * Utility class for encrypting and decrypting data.
 */
public class Cryptography {

    /**
     * XORs a byte array with a key.
     * @param input The byte array to XOR.
     * @param key The key to XOR with.
     * @return The XORed byte array.
     * @see PoolRegistry
     */
    private static byte[] xorWithKey(byte[] input, byte[] key) {
        byte[] output = new byte[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = (byte) (input[i] ^ key[i % key.length]);
        }
        return output;
    }
}
