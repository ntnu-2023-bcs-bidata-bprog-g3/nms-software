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

    public static final String POOL_REGISTRY_FILE_DIRECTORY_PATH = "poolreg.ser";
    public static final String POOL_REGISTRY_FILE_CHECKSUM_PATH = "poolreg.ser.md5";

    /**
     * Saves a PoolRegistry object to file.
     * @param poolreg The PoolRegistry object to save.
     * @return True if the PoolRegistry object was saved successfully, false otherwise.
     */
    private static boolean savePoolReg(PoolRegistry poolreg) {
        byte[] encrypted = Cryptography.encrypt(poolreg, KeyGenerator.generateKey());
        return FileHandler.writeToFile(encrypted, POOL_REGISTRY_FILE_DIRECTORY_PATH);
    }

    /**
     * Loads a PoolRegistry object from file.
     * @return The PoolRegistry object loaded from file.
     */
    private static PoolRegistry loadPoolReg() {
        byte[] encrypted = FileHandler.readFromFile(POOL_REGISTRY_FILE_DIRECTORY_PATH);
        return Cryptography.decrypt(encrypted, KeyGenerator.generateKey());
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
