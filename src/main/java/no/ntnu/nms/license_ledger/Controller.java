package no.ntnu.nms.license_ledger;

import no.ntnu.nms.file_handler.FileHandler;
import no.ntnu.nms.security.Checksum;
import no.ntnu.nms.security.Cryptography;
import no.ntnu.nms.security.KeyGenerator;
import org.springframework.util.SerializationUtils;

import java.nio.file.Files;
import java.nio.file.Path;

public class Controller {

    /**
     * The directory where the ledger and its checksum is stored.
     */
    private static final String LEDGER_DIR = "data/ledger/";


    /**
     * The name of the ledger file.
     */
    private static final String LEDGER_NAME = "ledger.txt";
    /**
     * The name of the ledger checksum file.
     */
    private static final String LEDGER_HASH_NAME = LEDGER_NAME + ".md5";


    /**
     * The complete path to the ledger file.
     */
    private static final String LEDGER_PATH = LEDGER_DIR + LEDGER_NAME;
    /**
     * The complete path to the ledger checksum file.
     */
    private static final String LEDGER_HASH_PATH = LEDGER_DIR + LEDGER_HASH_NAME;

    /*
    public static boolean createInitialLedgerFiles() {
        if (Files.notExists(Path.of(LEDGER_PATH))) {
            try {
                Files.createFile(Path.of(LEDGER_PATH));
            } catch (Exception e) {
                return false;
            }
        }
        if (Files.notExists(Path.of(LEDGER_HASH_PATH))) {
            return updateLedgerHash();
        }
        return true;
    }
    */

    /**
     * Gets the ledger checksum.
     * @return {@link String} The ledger checksum, null if the ledger does not exist.
     */
    private static String getLedgerHash() {
        return (Files.notExists(Path.of(LEDGER_PATH))) ?  null : (String) SerializationUtils.deserialize(Cryptography.xorWithKey(FileHandler.readFromFile(LEDGER_PATH), KeyGenerator.KEY));
    }

    /**
     * Checks if the ledger is invalid.
     * @return {@link Boolean} True if the ledger is invalid, false otherwise.
     */
    private static boolean ledgerNotValid() {
        String oldHash;
        if ((oldHash = getLedgerHash()) == null) return true;
        String currentHash = Checksum.generateFromFile(LEDGER_PATH);
        return !oldHash.equals(currentHash);
    }

    /**
     * Updates the ledger checksum.
     * @return {@link Boolean} True if the ledger checksum was updated successfully, false otherwise.
     */
    private static boolean updateLedgerHash() {
        if (ledgerNotValid()) return false;
        String checksum = Checksum.generateFromFile(LEDGER_PATH);
        if (checksum == null) return false;
        byte[] encryptedNewHash = Cryptography.xorWithKey(checksum.getBytes(), KeyGenerator.KEY);
        return FileHandler.writeToFile(encryptedNewHash, LEDGER_HASH_PATH);
    }

    /**
     * Adds a license to the ledger.
     * @param licensePath The path to the license file.
     * @return {@link Boolean} True if the license was added successfully, false otherwise.
     */
    public static boolean addLicenseToLedger(String licensePath) {
        if (ledgerNotValid()) return false;
        byte[] license = FileHandler.readFromFile(licensePath);
        if (license == null) return false;
        String licenseString = new String(license);
        byte[] oldLedger = Cryptography.xorWithKey(FileHandler.readFromFile(LEDGER_PATH), KeyGenerator.KEY);
        if (oldLedger == null) return false;
        String oldLedgerString = (String) SerializationUtils.deserialize(oldLedger);
        String newLedgerString = oldLedgerString + "\n" + licenseString;
        byte[] newLedger = Cryptography.xorWithKey(SerializationUtils.serialize(newLedgerString), KeyGenerator.KEY);
        if (!FileHandler.writeToFile(newLedger, LEDGER_PATH)) return false;
        return updateLedgerHash();
    }
}