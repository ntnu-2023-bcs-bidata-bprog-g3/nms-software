package no.ntnu.nms.licenseLedger;

import no.ntnu.nms.exception.CryptographyException;
import no.ntnu.nms.exception.FileHandlerException;
import no.ntnu.nms.exception.LedgerException;
import no.ntnu.nms.persistence.Controller;
import no.ntnu.nms.security.Checksum;


public abstract class LicenseLedgerController {

    /**
     * The complete path to the ledger file.
     */
    private String ledgerPath;

    protected void setLedgerPath(String ledgerDir) {
        this.ledgerPath = ledgerDir;
    }

    protected LicenseLedgerController(String ledgerDir) throws FileHandlerException {
        this.setLedgerPath(ledgerDir);
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
            throw new LedgerException("Failed to read license: " + licensePath);
        }
        try {
            Controller.appendToFile(checksum, ledgerPath);
        } catch (FileHandlerException e) {
            throw new LedgerException("Failed to append to ledger: " + e.getMessage());
        }
    }

    public String getLedger() throws LedgerException {
        return Controller.loadLedger(this.ledgerPath);
    }
}