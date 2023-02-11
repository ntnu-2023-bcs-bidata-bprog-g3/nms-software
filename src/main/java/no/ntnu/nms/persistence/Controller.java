package no.ntnu.nms.persistence;

import no.ntnu.nms.domain_model.PoolRegistry;
import no.ntnu.nms.exception.CryptographyException;
import no.ntnu.nms.exception.FileHandlerException;
import no.ntnu.nms.file_handler.FileHandler;
import no.ntnu.nms.security.Checksum;
import no.ntnu.nms.security.Cryptography;
import no.ntnu.nms.security.KeyGenerator;

import java.io.IOException;


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
     */
    public static void savePoolRegAndChecksum() throws FileHandlerException {
        try {
            FileHandler.backup(POOL_REGISTRY_FILE_CHECKSUM_PATH);
            FileHandler.backup(POOL_REGISTRY_FILE_DIRECTORY_PATH);
        } catch (IOException e) {
            throw new FileHandlerException("Failed to back up pool registry");
        }
        byte[] encryptedPoolRegistry, checksum;
        try {
            encryptedPoolRegistry = Cryptography.xorWithKey(SerializationUtils.serialize(PoolRegistry.getInstance()), KeyGenerator.KEY);
            FileHandler.writeToFile(encryptedPoolRegistry, POOL_REGISTRY_FILE_DIRECTORY_PATH);
            checksum = Cryptography.xorWithKey(Checksum.generateFromFile(POOL_REGISTRY_FILE_DIRECTORY_PATH).getBytes(), KeyGenerator.KEY);
        } catch (CryptographyException e) {
            throw new FileHandlerException("Failed to perform encryption " + e.getMessage());
        }
        FileHandler.writeToFile(checksum, POOL_REGISTRY_FILE_CHECKSUM_PATH);
        FileHandler.deleteBackup(POOL_REGISTRY_FILE_CHECKSUM_PATH);
        FileHandler.deleteBackup(POOL_REGISTRY_FILE_DIRECTORY_PATH);
    }

    /**
     * Loads a PoolRegistry object from file and checks the checksum.
     */
    public static void loadPoolRegAndCheckChecksum() {
        PoolRegistry poolreg = null;
        try {
            byte[] encrypted = FileHandler.readFromFile(POOL_REGISTRY_FILE_DIRECTORY_PATH);
            if (encrypted == null) throw new FileHandlerException("Failed to read pool registry");
            poolreg = (PoolRegistry) SerializationUtils.deserialize(Cryptography.xorWithKey(encrypted, KeyGenerator.KEY));
            if (poolreg == null) throw new FileHandlerException("Failed to decrypt pool registry");
            if (!Checksum.compare(POOL_REGISTRY_FILE_DIRECTORY_PATH, POOL_REGISTRY_FILE_CHECKSUM_PATH)) throw new FileHandlerException("Failed to compare old and new checksum");
        } catch (FileHandlerException e) {
            // TODO: ADD LOGGING USING e.message()
            System.out.println(e.getMessage());
            System.exit(1);
        }
        PoolRegistry.updatePoolRegistryInstance(poolreg);
    }
}
