package no.ntnu.nms;

import no.ntnu.nms.domainModel.PoolRegistry;
import no.ntnu.nms.license.LicenseLedger;


public class Init {
    public static void main(String[] args) {
        PoolRegistry.init("data/pool/poolreg.ser");
        LicenseLedger.init("data/ledger/top_license_ledger.txt");
    }
}
