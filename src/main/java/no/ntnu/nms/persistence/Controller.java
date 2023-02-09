package no.ntnu.nms.persistence;

import no.ntnu.nms.domain_model.PoolRegistry;
import no.ntnu.nms.file_handler.FileHandler;
import no.ntnu.nms.security.Checksum;
import no.ntnu.nms.security.Cryptography;
import no.ntnu.nms.security.KeyGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.util.SerializationUtils;

/**
 * Utility class for saving and loading PoolRegistry objects.
 */
public class Controller {

    private static final String POOL_REGISTRY_DIR = "data/pool/";

    private static final String POOL_REGISTRY_NAME = "poolreg.ser";
    private static final String POOL_REGISTRY_CHECKSUM_NAME = POOL_REGISTRY_NAME + ".md5";

    private static final String POOL_REGISTRY_FILE_DIRECTORY_PATH = POOL_REGISTRY_DIR
            + POOL_REGISTRY_NAME;
    private static final String POOL_REGISTRY_FILE_CHECKSUM_PATH = POOL_REGISTRY_DIR
            + POOL_REGISTRY_CHECKSUM_NAME;

    /**
     * Saves a PoolRegistry object to file and calculates a checksum, in addition to writing the
     * checksum to file.
     * @return {@link Boolean} True if the PoolRegistry object was saved successfully,
     * false otherwise.
     */
    public static boolean savePoolRegAndChecksum() {
        boolean successSave = savePoolReg();
        boolean successWriteChecksum = true;
        if (successSave) {
            String checksum = Checksum.generateFromFile(POOL_REGISTRY_FILE_DIRECTORY_PATH);
            successWriteChecksum = FileHandler.writeToFile(Cryptography.xorWithKey(checksum.getBytes(),
                    KeyGenerator.KEY), POOL_REGISTRY_FILE_CHECKSUM_PATH);
        }
        if (!successWriteChecksum || !successSave) {
            // TODO: ADD LOGGING!!!!
            System.out.println("Major important error!");
            return false;
        }
        return true;
    }

    /**
     * Loads a PoolRegistry object from file and checks the checksum.
     * @return {@link PoolRegistry} The PoolRegistry object loaded from file, null if failed.
     */
    public static PoolRegistry loadPoolRegAndCheckChecksum() {
        PoolRegistry poolreg = loadPoolReg();
        if (poolreg == null) return null;
        if (!Checksum.compare(POOL_REGISTRY_FILE_DIRECTORY_PATH,
                POOL_REGISTRY_FILE_CHECKSUM_PATH)) return null;
        PoolRegistry.updatePoolRegistryInstance(poolreg);
        return poolreg;
    }

    /**
     * Saves a PoolRegistry object to file.
     * @return True if the PoolRegistry object was saved successfully, false otherwise.
     */
    private static boolean savePoolReg() {
        byte[] encrypted = Cryptography.xorWithKey(SerializationUtils.serialize(PoolRegistry.getInstance()), KeyGenerator.KEY);
        return FileHandler.writeToFile(encrypted, POOL_REGISTRY_FILE_DIRECTORY_PATH);
    }

    /**
     * Loads a PoolRegistry object from file.
     * @return The PoolRegistry object loaded from file.
     */
    private static PoolRegistry loadPoolReg() {
        byte[] encrypted = FileHandler.readFromFile(POOL_REGISTRY_FILE_DIRECTORY_PATH);
        return (PoolRegistry) SerializationUtils.deserialize(((byte[]) Cryptography.xorWithKey(encrypted, KeyGenerator.KEY)));
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
