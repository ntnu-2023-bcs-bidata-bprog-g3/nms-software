package no.ntnu.nms.licenseLedger;

public class TopLicenseLedger extends LicenseLedgerController {

    private static TopLicenseLedger instance = null;

    private static final String LEDGER_PATH = "data/ledger/top_license_ledger";

    private TopLicenseLedger(String ledgerDir) {
        super(ledgerDir);
    }

    public static TopLicenseLedger getInstance() {
        if (instance == null) {
            instance = new TopLicenseLedger(LEDGER_PATH);
        }
        return instance;
    }


}
