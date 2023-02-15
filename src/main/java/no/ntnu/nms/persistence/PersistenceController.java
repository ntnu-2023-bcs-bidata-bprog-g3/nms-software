package no.ntnu.nms.persistence;

import no.ntnu.nms.domainModel.PoolRegistry;
import no.ntnu.nms.exception.CryptographyException;
import no.ntnu.nms.exception.FileHandlerException;
import no.ntnu.nms.exception.LedgerException;
import no.ntnu.nms.file_handler.FileHandler;
import no.ntnu.nms.logging.Logging;
import no.ntnu.nms.security.Checksum;
import no.ntnu.nms.security.Cryptography;
import no.ntnu.nms.security.KeyGenerator;

import java.io.IOException;

import org.springframework.util.SerializationUtils;

/**
 * Utility class for saving and loading PoolRegistry objects.
 */
public class PersistenceController {

    /**
     * Saves a PoolRegistry object to file and calculates a checksum, in addition to writing the
     * checksum to file.
     */
    public static void saveToFile(Object objectToSerialize, String filePath, boolean includeHash) throws FileHandlerException {
        String checksumPath = filePath + ".md5";
        try {
            if (includeHash) {
                FileHandler.backup(checksumPath);
            }
            FileHandler.backup(filePath);
        } catch (IOException e) {
            Logging.getLogger().warning("Failed to create backup: " + e.getMessage());
            throw new FileHandlerException("Failed to back up file: " + e.getMessage());
        }
        byte[] encryptedPoolRegistry, checksum;
        try {
            encryptedPoolRegistry = Cryptography.xorWithKey(SerializationUtils.serialize(objectToSerialize), KeyGenerator.KEY);
            FileHandler.writeToFile(encryptedPoolRegistry, filePath);
            checksum = Cryptography.xorWithKey(Checksum.generateFromFile(filePath).getBytes(), KeyGenerator.KEY);
        } catch (CryptographyException e) {
            Logging.getLogger().warning("Failed to perform encryption " + e.getMessage());
            throw new FileHandlerException("Failed to perform encryption " + e.getMessage());
        }
        if (includeHash) {
            FileHandler.writeToFile(checksum, checksumPath);
        }
        FileHandler.deleteBackup(checksumPath);
        FileHandler.deleteBackup(filePath);
    }

    public static void appendToFile(String toAppend, String filePath) throws FileHandlerException {
        String oldLedgerString;
        byte[] newLedger;
        try {
            oldLedgerString = loadLedger(filePath);
        } catch(LedgerException e) {
            Logging.getLogger().warning("Failed tp load old ledger: " + e.getMessage());
            throw new FileHandlerException("Failed to load old ledger: " + e.getMessage());
        }
        String newLedgerString = oldLedgerString + "\n" + toAppend;

        try {
            newLedger = Cryptography.xorWithKey(SerializationUtils.serialize(newLedgerString), KeyGenerator.KEY);
        } catch (CryptographyException e) {
            Logging.getLogger().warning("Failed to append to file: "+ e.getMessage());
            throw new FileHandlerException("Failed to append to file: " + e.getMessage());
        }
        FileHandler.writeToFile(newLedger, filePath);
    }

    public static String loadLedger(String filePath) throws LedgerException {
        byte[] encryptedFileContent;
        try {
            encryptedFileContent = FileHandler.readFromFile(filePath);
        } catch (FileHandlerException e) {
            Logging.getLogger().warning("Failed to read ledger from file: " + e.getMessage());
            throw new LedgerException("Failed to read ledger from file: " + e.getMessage());
        }
        byte[] decryptedFileContent;
        try {
            decryptedFileContent = Cryptography.xorWithKey(encryptedFileContent, KeyGenerator.KEY);
        } catch (CryptographyException e) {
            Logging.getLogger().warning("Failed to decrypt ledger: " + e.getMessage());
            throw new LedgerException("Failed to decrypt ledger: " + e.getMessage());
        }
        return (String) SerializationUtils.deserialize(decryptedFileContent);
    }

    /**
     * Loads a PoolRegistry object from file and checks the checksum.
     */
    public static void loadFromFile(String filePath) {
        PoolRegistry poolreg = null;
        String checksumPath = filePath + ".md5";
        try {
            byte[] encrypted = FileHandler.readFromFile(filePath);
            if (encrypted == null) throw new FileHandlerException("Failed to read pool registry");
            poolreg = (PoolRegistry) SerializationUtils.deserialize(Cryptography.xorWithKey(encrypted, KeyGenerator.KEY));
            if (poolreg == null) throw new FileHandlerException("Failed to decrypt pool registry");
            if (!Checksum.compare(filePath, checksumPath)) throw new FileHandlerException("Failed to compare old and new checksum");
        } catch (FileHandlerException e) {
            Logging.getLogger().severe("Unable to load PoolRegistry from file. Core functionality has been affected. Error: " + e.getMessage());
            System.exit(1);
        }
        PoolRegistry.updatePoolRegistryInstance(poolreg);
    }
}
