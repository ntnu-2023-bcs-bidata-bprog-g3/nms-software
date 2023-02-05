package no.ntnu.nms.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for generating a key for encryption/decryption.
 */
public class KeyGenerator {

    // The key used for encryption/decryption.
    public static final byte[] KEY = generateKey();


    // The number of iterations to perform when generating the key.
    private static final int ITERATION_COUNT = 1000;
    // The length of the key in bits.
    private static final int KEY_LENGTH = 256;
    // The seed used to generate the key.
    private static final int SEED = 230500;
    // The magic number used to generate the key.
    private static final int MAGIC_NUMBER = 69;


    /*
     * Generates a key for encryption/decryption.
     * @return The generated key.
     * @throws RuntimeException if the SHA-256 algorithm is not available.
     */
    public static byte[] generateKey() {
        byte[] salt = new byte[16];
        byte[] key = new byte[KEY_LENGTH / 8];
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            digest.update(salt);

            int seed = SEED;
            for (int i = 0; i < ITERATION_COUNT; i++) {
                seed = seed * MAGIC_NUMBER + i;
            }
            byte[] input = digest.digest(String.valueOf(seed).getBytes());
            for (int i = 0; i < ITERATION_COUNT; i++) {
                digest.reset();
                input = digest.digest(input);
            }
            System.arraycopy(input, 0, key, 0, key.length);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return key;
    }
}
