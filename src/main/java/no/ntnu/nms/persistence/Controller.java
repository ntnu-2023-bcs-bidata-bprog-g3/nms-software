package no.ntnu.nms.persistence;

import no.ntnu.nms.domain_model.PoolRegistry;
import no.ntnu.nms.security.Checksum;
import no.ntnu.nms.security.Cryptography;
import no.ntnu.nms.security.KeyGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for saving and loading PoolRegistry objects.
 */
public class Controller {

    public static final String POOL_REGISTRY_FILE_DIRECTORY_PATH = "pools/poolreg.ser";
    public static final String POOL_REGISTRY_FILE_CHECKSUM_PATH = "pools/poolreg.ser.md5";

    /**
     * Saves a PoolRegistry object to file and calculates a checksum, in addition to writing the
     * checksum to file.
     * @return {@link Boolean} True if the PoolRegistry object was saved successfully,
     * false otherwise.
     */
    public static boolean savePoolRegAndChecksum() {
        deletePoolReg();    // Deletes the old pool file. If it is deleted, but failed to write new,
                            // the old would be outdated anyway
        boolean success = savePoolReg();
        if (success) {
            try {
                String checksum = Checksum.checksum(POOL_REGISTRY_FILE_DIRECTORY_PATH);
                success = FileHandler.writeToFile(Cryptography.encrypt(checksum.getBytes(),
                        KeyGenerator.KEY), POOL_REGISTRY_FILE_CHECKSUM_PATH);
            } catch (RuntimeException e) {
                return false;
            }
        }
        if (!success) {
            // TODO: ADD LOGGING!!!!
            System.out.println("Major important error!");
        }
        return success;
    }

    /**
     * Loads a PoolRegistry object from file and checks the checksum.
     * @return {@link PoolRegistry} The PoolRegistry object loaded from file, null if failed.
     */
    public static PoolRegistry loadPoolRegAndCheckChecksum() {
        PoolRegistry poolreg = loadPoolReg();
        if (poolreg != null) {
            try {
                String checksum = Checksum.checksum(POOL_REGISTRY_FILE_DIRECTORY_PATH);
                byte[] encryptedChecksum = FileHandler.readFromFile(POOL_REGISTRY_FILE_CHECKSUM_PATH);
                String checksumFromFile = new String(Cryptography.decryptBytes(encryptedChecksum,
                        KeyGenerator.KEY));
                if (!checksum.equals(checksumFromFile)) {
                    return null;
                }
            } catch (RuntimeException e) {
                return null;
            }
        }
        return poolreg;
    }

    /**
     * Saves a PoolRegistry object to file.
     * @return True if the PoolRegistry object was saved successfully, false otherwise.
     */
    private static boolean savePoolReg() {
        byte[] encrypted = Cryptography.encrypt(PoolRegistry.getInstance(), KeyGenerator.KEY);
        return FileHandler.writeToFile(encrypted, POOL_REGISTRY_FILE_DIRECTORY_PATH);
    }

    /**
     * Loads a PoolRegistry object from file.
     * @return The PoolRegistry object loaded from file.
     */
    private static PoolRegistry loadPoolReg() {
        byte[] encrypted = FileHandler.readFromFile(POOL_REGISTRY_FILE_DIRECTORY_PATH);
        return Cryptography.decrypt(encrypted, KeyGenerator.KEY);
    }

    /**
     * Deletes the PoolRegistry file.
     */
    private static void deletePoolReg() {
        Path path = Paths.get(POOL_REGISTRY_FILE_DIRECTORY_PATH);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}