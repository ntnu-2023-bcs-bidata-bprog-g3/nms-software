package no.ntnu.nms.security;

import no.ntnu.nms.exception.CryptographyException;
import no.ntnu.nms.exception.FileHandlerException;
import no.ntnu.nms.file_handler.FileHandler;
import no.ntnu.nms.logging.Logging;

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

    public static String generateFromFile(String path) throws CryptographyException {
        byte[] data, hash;
        try {
            data = Files.readAllBytes(Paths.get(path));
            hash = MessageDigest.getInstance("MD5").digest(data);
            return new BigInteger(1, hash).toString(16);
        } catch (IOException e) {
            Logging.getLogger().warning("Failed to read file: " + e.getMessage());
            throw new CryptographyException("Failed to read file: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            Logging.getLogger().warning("Failed to create message digester: " + e.getMessage());
            throw new CryptographyException("Failed to create message digester: " + e.getMessage());
        } catch (NumberFormatException e) {
            Logging.getLogger().warning("Failed to create hash: " + e.getMessage());
            throw new CryptographyException("Failed to create hash: " + e.getMessage());
        }
    }

    /**
     * Compares the checksum of a file to the checksum stored in another file.
     * @param fileDirectory The path to the file to calculate the checksum of.
     * @param checksumDirectory The path to the file containing the checksum.
     * @return {@link Boolean} True if the checksums match, false otherwise.
     */
    public static boolean compare(String fileDirectory, String checksumDirectory) {
        try {
            String checksum = Checksum.generateFromFile(fileDirectory);
            byte[] decryptedChecksumFromFile = Cryptography.xorWithKey(FileHandler
                    .readFromFile(checksumDirectory), KeyGenerator.KEY);
            String checksumFromFile = new String(decryptedChecksumFromFile);
            return checksum.equals(checksumFromFile);
        } catch (CryptographyException | FileHandlerException e) {
            Logging.getLogger().severe("Unable to compare checksums. Core functionality affected.");
            System.exit(1);
        }
        return true;
    }
}
