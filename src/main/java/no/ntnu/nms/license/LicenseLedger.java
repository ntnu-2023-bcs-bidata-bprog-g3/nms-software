package no.ntnu.nms.license;

import no.ntnu.nms.exception.CryptographyException;
import no.ntnu.nms.exception.FileHandlerException;
import no.ntnu.nms.exception.LedgerException;
import no.ntnu.nms.logging.Logging;
import no.ntnu.nms.persistence.PersistenceController;
import no.ntnu.nms.security.Checksum;

/**
 * A ledger for storing the checksums of all licenses redeemed.
 */
public class LicenseLedger {

    /**
     * The singleton instance of the ledger.
     */
    private static LicenseLedger instance = null;

    /**
     * The complete path to the ledger file.
     */
    private String ledgerPath;

    /**
     * The path to the ledger file.
     */
    private static final String LEDGER_PATH = "data/ledger/top_license_ledger.txt";

    /**
     * Init function used for setting up the storage files.
     * @param ledgerPath The path to the ledger file.
     */
    public static void init(String ledgerPath) {
        if (ledgerPath == null) {
            instance = new LicenseLedger(LEDGER_PATH);
            PersistenceController.saveToFile("", LEDGER_PATH, false);
        } else {
            instance = new LicenseLedger(ledgerPath);
            PersistenceController.saveToFile("", ledgerPath, false);
        }
    }

    /**
     * Private constructor to prevent instantiation.
     * @param ledgerDir The path to the ledger file.
     * @throws FileHandlerException If the ledger file cannot be created.
     */
    private LicenseLedger(String ledgerDir) throws FileHandlerException {
        this.setLedgerPath(ledgerDir);
    }

    /**
     * Getter for the singleton instance.
     * @return The singleton instance.
     */
    public static LicenseLedger getInstance() {
        if (instance == null) {
            instance = new LicenseLedger(LEDGER_PATH);
        }
        return instance;
    }

    /**
     * Setter for the ledger path.
     * @param ledgerDir The path to the ledger file.
     */
    protected void setLedgerPath(String ledgerDir) {
        this.ledgerPath = ledgerDir;
    }

    /**
     * Adds a license to the ledger.
     * @param licensePath The path to the license file.
     */
    public void addLicenseToLedger(String licensePath) throws LedgerException {
        String checksum;
        try {
            checksum = Checksum.generateFromFile(licensePath);
        } catch (CryptographyException e) {
            Logging.getLogger().warning("Failed to read license: " + licensePath);
            throw new LedgerException("Failed to read license: " + licensePath);
        }
        try {
            PersistenceController.appendToFile(checksum, ledgerPath);
        } catch (FileHandlerException e) {
            Logging.getLogger().warning("Failed to append to ledger: " + e.getMessage());
            throw new LedgerException("Failed to append to ledger: " + e.getMessage());
        }
    }

    /**
     * Checks if a license is in the ledger.
     * @param licensePath The path to the license file.
     * @return True if the license is in the ledger, false otherwise.
     * @throws LedgerException If the ledger cannot be loaded.
     */
    public boolean licenseIsInLedger(String licensePath) throws LedgerException {
        String checksum;
        try {
            checksum = Checksum.generateFromFile(licensePath);
        } catch (CryptographyException e) {
            Logging.getLogger().warning("Failed to read license: " + licensePath);
            throw new LedgerException("Failed to read license: " + licensePath);
        }
        return PersistenceController.loadLedger(ledgerPath).contains(checksum);
    }

    /**
     * Getter for the ledger.
     * @return  The ledger as a string.
     * @throws LedgerException If the ledger cannot be loaded.
     */
    public String getLedger() throws LedgerException {
        return PersistenceController.loadLedger(this.ledgerPath);
    }
}