package no.ntnu.nms.security;

/*
 * Utility class for encrypting and decrypting data.
 */
public class Cryptography {

    /**
     * XORs a byte array with a key.
     * @param input The byte array to XOR.
     * @param key The key to XOR with.
     * @return The XORed byte array.
     */
    public static byte[] xorWithKey(byte[] input, byte[] key) {
        if (input == null) return null;
        
        byte[] output = new byte[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = (byte) (input[i] ^ key[i % key.length]);
        }
        return output;
    }

}
