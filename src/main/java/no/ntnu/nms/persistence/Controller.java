package no.ntnu.nms.persistence;

import no.ntnu.nms.domain_model.PoolRegistry;
import no.ntnu.nms.security.Cryptography;
import no.ntnu.nms.security.KeyGenerator;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Controller {

    public static final String POOL_REGISTRY_FILE_DIRECTORY_PATH = "poolreg.ser";
    public static final String POOL_REGISTRY_FILE_CHECKSUM_PATH = "poolreg.ser.md5";

    /**
     * Saves a PoolRegistry object to file.
     * @param poolreg The PoolRegistry object to save.
     * @return True if the PoolRegistry object was saved successfully, false otherwise.
     */
    public static boolean savePoolReg(PoolRegistry poolreg) {
        byte[] encrypted = Cryptography.encrypt(poolreg, KeyGenerator.generateKey());
        return FileHandler.writeToFile(encrypted, POOL_REGISTRY_FILE_DIRECTORY_PATH);
    }

    /**
     * Loads a PoolRegistry object from file.
     * @return The PoolRegistry object loaded from file.
     */
    public static PoolRegistry loadPoolReg() {
        byte[] encrypted = FileHandler.readFromFile(POOL_REGISTRY_FILE_DIRECTORY_PATH);
        return Cryptography.decrypt(encrypted, KeyGenerator.generateKey());
    }

    //function that calculates checksum of a file
    public static String checksum(String path) throws RuntimeException {
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
}
