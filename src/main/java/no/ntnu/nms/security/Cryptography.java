package no.ntnu.nms.security;

import no.ntnu.nms.exception.CryptographyException;

/**
 * Utility class for encrypting and decrypting data.
 */
public class Cryptography {

    /**
     * XORs a byte array with a key.
     * @param input The byte array to XOR.
     * @param key The key to XOR with.
     * @return The XORed byte array.
     */
    public static byte[] xorWithKey(byte[] input, byte[] key) throws CryptographyException {
        if (input == null) throw new CryptographyException("Failed to decrypt/encrypt: input is null");
        
        byte[] output = new byte[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = (byte) (input[i] ^ key[i % key.length]);
        }
        return output;
    }

}
