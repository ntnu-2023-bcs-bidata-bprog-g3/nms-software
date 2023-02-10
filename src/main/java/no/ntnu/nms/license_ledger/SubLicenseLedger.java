package no.ntnu.nms.license_ledger;

public class SubLicenseLedger extends LicenseLedgerController {

    private static SubLicenseLedger instance = null;

    private static final String LEDGER_PATH = "data/ledger/sub_license_ledger";

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
