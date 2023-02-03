package no.ntnu.nms.security;

import org.springframework.util.SerializationUtils;

import no.ntnu.nms.domain_model.PoolRegistry;

import java.util.Objects;

/*
 * Utility class for encrypting and decrypting data.
 */
public class Cryptography {

    /**
     * Encrypts a PoolRegistry object.
     * @param obj The PoolRegistry object to encrypt.
     * @param key The key to encrypt with.
     * @return The encrypted PoolRegistry object.
     * @see PoolRegistry
     */
    public static byte[] encrypt(Object obj, byte[] key) {
        if (obj instanceof PoolRegistry) {
            return xorWithKey(Objects.requireNonNull(SerializationUtils.serialize(obj)), key);
        }
        return xorWithKey((byte[]) obj, key);
    }

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

    /**
     * Decrypts a byte array.
     * @param encrypted
     * @param key
     * @return The decrypted PoolRegistry object.
     */
    public static PoolRegistry decrypt(byte[] encrypted, byte[] key) {
        if (encrypted == null) return null;

        //decrypt data with key
        byte[] decrypted = xorWithKey(encrypted, key);

        //convert byte array to poolreg
        PoolRegistry poolreg = (PoolRegistry) SerializationUtils.deserialize(decrypted);

        return poolreg;
    }

    /**
     * Decrypts a byte array.
     * @param encrypted
     * @param key
     * @return The decrypted byte array.
     */
    public static byte[] decryptBytes(byte[] encrypted, byte[] key) {
        if (encrypted == null) return null;
        return xorWithKey(encrypted, key);
    }
}
