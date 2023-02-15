package no.ntnu.nms.licenseLedger;

import no.ntnu.nms.persistence.Controller;

public class TopLicenseLedger extends LicenseLedgerController {

    private static TopLicenseLedger instance = null;

    private static final String LEDGER_PATH = "data/ledger/top_license_ledger.txt";

    private TopLicenseLedger(String ledgerDir) {
        super(ledgerDir);
    }

    public static void init() {
        if (instance == null) {
            instance = new TopLicenseLedger(LEDGER_PATH);
            Controller.saveToFile("", LEDGER_PATH, false);
        }
    }

    public static TopLicenseLedger getInstance() {
        if (instance == null) {
            instance = new TopLicenseLedger(LEDGER_PATH);
        }
        return instance;
    }


}
