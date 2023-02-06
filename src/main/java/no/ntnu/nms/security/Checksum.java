package no.ntnu.nms.security;

import no.ntnu.nms.persistence.FileHandler;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for calculating checksums.
 */
public class Checksum {
    /**
     * Calculates the checksum of a file.
     * @param path {@link String} The path to the file to calculate the checksum of.
     * @return {@link String} The checksum of the file.
     */

    public static String loadFromFile(String path) {
        byte[] data, hash;
        try {
            data = Files.readAllBytes(Paths.get(path));
            hash = MessageDigest.getInstance("MD5").digest(data);
            return new BigInteger(1, hash).toString(16);
        } catch (IOException | NoSuchAlgorithmException | NumberFormatException e) {
            //TODO: Add logging
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Compares the checksum of a file to the checksum stored in another file.
     * @param fileDirectory The path to the file to calculate the checksum of.
     * @param checksumDirectory The path to the file containing the checksum.
     * @return {@link Boolean} True if the checksums match, false otherwise.
     */
    public static boolean compare(String fileDirectory, String checksumDirectory) {
        String checksum = Checksum.loadFromFile(fileDirectory);
        byte[] decryptedChecksumFromFile = Cryptography.xorWithKey(FileHandler
                .readFromFile(checksumDirectory), KeyGenerator.KEY);
        if (decryptedChecksumFromFile == null || checksum == null) {
            return false;
        }
        String checksumFromFile = new String(decryptedChecksumFromFile);
        return checksum.equals(checksumFromFile);
    }
}
