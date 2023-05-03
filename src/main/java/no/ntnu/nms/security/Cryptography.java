package no.ntnu.nms.security;

import no.ntnu.nms.exception.CryptographyException;
import no.ntnu.nms.logging.Logging;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Utility class for encrypting and decrypting data.
 */
public class Cryptography {
    public enum Mode {
        ENCRYPT,
        DECRYPT
    }
    // The key used for encryption/decryption.
    public static final byte[] KEY = generateKey();

    /**
     * Generates a key for encryption/decryption.
     * @return The generated key.
     * @throws RuntimeException if the AES algorithm is not available.
     */
    public static byte[] generateKey() throws CryptographyException{
        try {
            javax.crypto.KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(123456L); // Set a fixed seed for the random number generator, can be e.g. customer ID
            keyGen.init(256, random); // key length of 256 bits
            SecretKey secretKey = keyGen.generateKey();
            return secretKey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            Logging.getLogger().warning("Failed to generate key: " + e.getMessage());
            throw new CryptographyException("Failed to generate key: " + e.getMessage());
        }
    }

    /**
     * XORs a byte array with a key.
     * @param input The byte array to XOR.
     * @param key The key to XOR with.
     * @return The XORed byte array.
     */
    public static byte[] xorWithKey(byte[] input, byte[] key) throws CryptographyException {
        if (input == null) {
            throw new CryptographyException("Failed to decrypt/encrypt: input is null");
        }

        byte[] output = new byte[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = (byte) (input[i] ^ key[i % key.length]);
        }
        return output;
    }

    /**
     * Encrypts/decrypts a message using AES encryption.
     * @param message The message to encrypt.
     * @param mode The mode to use, e.g. ENCRYPT or DECRYPT
     * @return The encrypted message.
     */
    public static byte[] applyCipher(byte[] message, Mode mode) throws CryptographyException {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(KEY, "AES");
            Cipher cipher = Cipher.getInstance("AES");

            int modeInt = mode == Mode.ENCRYPT ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE;
            cipher.init(modeInt, keySpec);
            return cipher.doFinal(message);
        } catch (Exception e) {
            Logging.getLogger().warning("Failed to apply cipher:" + e.getMessage());
            throw new CryptographyException("Failed to apply cipher:" + e.getMessage());
        }
    }

}
