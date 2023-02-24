package no.ntnu.nms;

import no.ntnu.nms.domainModel.PoolRegistry;
import no.ntnu.nms.licenseLedger.LicenseLedger;


public class Init {
    public static void main(String[] args) {
        PoolRegistry.init("test/pool_test.ser");
        LicenseLedger.init("test/ledger_test.txt");
    }
}
