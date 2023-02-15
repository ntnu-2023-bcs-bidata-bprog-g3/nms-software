package no.ntnu.nms.licenseLedger;

import no.ntnu.nms.persistence.Controller;

public class SubLicenseLedger extends LicenseLedgerController {

    private static SubLicenseLedger instance = null;

    private static final String LEDGER_PATH = "data/ledger/sub_license_ledger.txt";

    public static void init() {
        if (instance == null) {
            instance = new SubLicenseLedger(LEDGER_PATH);
            Controller.saveToFile("", LEDGER_PATH, false);
        }
    }

    private SubLicenseLedger(String ledgerDir) {
        super(ledgerDir);
    }

    public static SubLicenseLedger getInstance() {
        if (instance == null) {
            instance = new SubLicenseLedger(LEDGER_PATH);
        }
        return instance;
    }


}
