package no.ntnu.nms.license_ledger;

import no.ntnu.nms.exception.FileHandlerException;
import no.ntnu.nms.exception.LedgerException;
import no.ntnu.nms.file_handler.FileHandler;
import no.ntnu.nms.security.Checksum;
import no.ntnu.nms.security.Cryptography;
import no.ntnu.nms.security.KeyGenerator;
import org.apache.commons.io.FileExistsException;
import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class LicenseLedgerController {

    protected static final String LEDGER_NAME = "ledger.txt";
    protected static final String LEDGER_HASH_NAME = "ledger.txt.md5";

    /**
     * The complete path to the ledger file.
     */
    private String ledgerPath;
    /**
     * The complete path to the ledger checksum file.
     */
    private String ledgerHashPath;

    protected void setLedgerPath(String ledgerDir) {
        this.ledgerPath = ledgerDir + LEDGER_NAME;
    }

    protected void setLedgerHashPath(String ledgerDir) {
        this.ledgerHashPath = ledgerDir + LEDGER_HASH_NAME;
    }

    protected LicenseLedgerController(String ledgerDir) throws FileHandlerException {
        this.setLedgerPath(ledgerDir);
        this.setLedgerHashPath(ledgerDir);
        try {
            this.createInitialLedgerFiles();
        } catch (FileExistsException e) {
            throw new FileHandlerException(e.getMessage());
        }
    }

    private void createInitialLedgerFiles() throws FileExistsException {
        if (Files.exists(Path.of(this.ledgerPath)) || Files.exists(Path.of(this.ledgerHashPath))) {
            throw new FileExistsException("Files for this ledger already exists in "
                    + ledgerPath.substring(ledgerPath.lastIndexOf('/') + 1));
        }
        try {
            Files.createFile(Path.of(this.ledgerPath));
        } catch (IOException e) {
            throw new FileExistsException("Failed to create ledger files. " + e.getMessage());
        }
        updateLedgerHash();
    }


    /**
     * Gets the ledger checksum.
     * @return {@link String} The ledger checksum, null if the ledger does not exist.
     */
    private String getLedgerHash() {
        return (Files.notExists(Path.of(ledgerPath))) ?  null : (String) SerializationUtils.deserialize(Cryptography.xorWithKey(FileHandler.readFromFile(ledgerPath), KeyGenerator.KEY));
    }

    /**
     * Checks if the ledger is invalid.
     * @return {@link Boolean} True if the ledger is invalid, false otherwise.
     */
    private boolean ledgerNotValid() {
        String oldHash;
        if ((oldHash = getLedgerHash()) == null) return true;
        String currentHash = Checksum.generateFromFile(ledgerPath);
        return !oldHash.equals(currentHash);
    }

    /**
     * Updates the ledger checksum.
     * @return {@link Boolean} True if the ledger checksum was updated successfully, false otherwise.
     */
    private void updateLedgerHash() {
        String checksum = Checksum.generateFromFile(ledgerPath);
        if (checksum == null) throw new LedgerException("");
        byte[] encryptedNewHash = Cryptography.xorWithKey(checksum.getBytes(), KeyGenerator.KEY);
        FileHandler.writeToFile(encryptedNewHash, ledgerHashPath);
    }

    /**
     * Adds a license to the ledger.
     * @param licensePath The path to the license file.
     * @return {@link Boolean} True if the license was added successfully, false otherwise.
     */
    public boolean addLicenseToLedger(String licensePath) {
        if (ledgerNotValid()) return false;
        byte[] license = FileHandler.readFromFile(licensePath);
        if (license == null) return false;
        String licenseString = new String(license);
        byte[] oldLedger = Cryptography.xorWithKey(FileHandler.readFromFile(ledgerPath), KeyGenerator.KEY);
        if (oldLedger == null) return false;
        String oldLedgerString = (String) SerializationUtils.deserialize(oldLedger);
        String newLedgerString = oldLedgerString + "\n" + licenseString;
        byte[] newLedger = Cryptography.xorWithKey(SerializationUtils.serialize(newLedgerString), KeyGenerator.KEY);
        FileHandler.writeToFile(newLedger, ledgerPath);
        updateLedgerHash();
        return true;
    }
}