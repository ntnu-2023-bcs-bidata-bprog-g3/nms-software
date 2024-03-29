package no.ntnu.nms.persistence;

import no.ntnu.nms.domainmodel.PoolRegistry;
import no.ntnu.nms.exception.CryptographyException;
import no.ntnu.nms.exception.FileHandlerException;
import no.ntnu.nms.exception.LedgerException;
import no.ntnu.nms.filehandler.FileHandler;
import no.ntnu.nms.logging.Logging;
import no.ntnu.nms.security.Checksum;
import no.ntnu.nms.security.Cryptography;
import java.io.IOException;

import org.springframework.util.SerializationUtils;

/**
 * Utility class for saving and loading PoolRegistry objects.
 */
public class PersistenceController {

    /**
     * Saves a PoolRegistry object to file and calculates a checksum, in addition to writing the
     * checksum to file.
     * @param filePath The path to the file to save to.
     * @param includeHash Whether to include a checksum.
     * @param objectToSerialize The object to serialize.
     * @throws FileHandlerException If the file could not be saved.
     */
    public static void saveToFile(Object objectToSerialize, String filePath, boolean includeHash)
            throws FileHandlerException {
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

        byte[] encryptedPoolRegistry;
        byte[] checksum;

        try {
            encryptedPoolRegistry = Cryptography.applyCipher(
                    SerializationUtils.serialize(objectToSerialize), Cryptography.Mode.ENCRYPT);
            FileHandler.writeToFile(encryptedPoolRegistry, filePath);
            checksum = Cryptography.applyCipher(
                    Checksum.generateFromFile(filePath).getBytes(), Cryptography.Mode.ENCRYPT);
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

    /**
     * Appends a string to a file.
     * @param toAppend The string to append.
     * @param filePath The path to the file to append to.
     * @throws FileHandlerException If the file could not be appended to.
     */
    public static void appendToFile(String toAppend, String filePath) throws FileHandlerException {
        String oldLedgerString;
        byte[] newLedger;
        try {
            oldLedgerString = loadLedger(filePath);
        } catch (LedgerException e) {
            Logging.getLogger().warning("Failed tp load old ledger: " + e.getMessage());
            throw new FileHandlerException("Failed to load old ledger: " + e.getMessage());
        }
        String newLedgerString = oldLedgerString + "\n" + toAppend;

        try {
            newLedger = Cryptography.applyCipher(
                    SerializationUtils.serialize(newLedgerString), Cryptography.Mode.ENCRYPT);
        } catch (CryptographyException e) {
            Logging.getLogger().warning("Failed to append to file: " + e.getMessage());
            throw new FileHandlerException("Failed to append to file: " + e.getMessage());
        }
        FileHandler.writeToFile(newLedger, filePath);
    }

    /**
     * Loads a ledger from file.
     * @param filePath The path to the file to load from.
     * @return The ledger as a string.
     * @throws LedgerException If the ledger could not be loaded.
     */
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
            decryptedFileContent = Cryptography.applyCipher(encryptedFileContent,
                    Cryptography.Mode.DECRYPT);
        } catch (CryptographyException e) {
            Logging.getLogger().warning("Failed to decrypt ledger: " + e.getMessage());
            throw new LedgerException("Failed to decrypt ledger: " + e.getMessage());
        }
        return (String) SerializationUtils.deserialize(decryptedFileContent);
    }

    /**
     * Loads a PoolRegistry object from file and checks the checksum.
     * @param filePath The path to the file to load from.
     */
    public static void loadFromFile(String filePath) {
        PoolRegistry poolreg = null;
        try {
            byte[] encrypted = FileHandler.readFromFile(filePath);
            if (encrypted == null) {
                throw new FileHandlerException("Failed to read pool registry");
            }
            poolreg = (PoolRegistry) SerializationUtils.deserialize(
                    Cryptography.applyCipher(encrypted, Cryptography.Mode.DECRYPT));
            if (poolreg == null) {
                throw new FileHandlerException("Failed to decrypt pool registry");
            }
        } catch (FileHandlerException e) {
            Logging.getLogger().severe(
                    "Core functionality has been affected. Error: " + e.getMessage());
            System.exit(1);
        }
        PoolRegistry.updatePoolRegistryInstance(poolreg);
    }
}
