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
    public static String getChecksumFromFile(String path) {
        byte[] data;
        try {
            data = Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: Add logging
            return null;
        }
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(data);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            // TODO: Add logging
            return null;
        }
        try {
            return new BigInteger(1, hash).toString(16);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            // TODO: Add logging
            return null;
        }
    }

    public static boolean compareChecksum(String fileDirectory, String checksumDirectory) {
        String checksum = Checksum.getChecksumFromFile(fileDirectory);
        byte[] decryptedChecksumFromFile = Cryptography.decryptBytes(FileHandler
                .readFromFile(checksumDirectory), KeyGenerator.KEY);
        if (decryptedChecksumFromFile == null || checksum == null) {
            return false;
        }
        String checksumFromFile = new String(decryptedChecksumFromFile);
        return checksum.equals(checksumFromFile);
    }
}
