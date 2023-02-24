package no.ntnu.nms;

import no.ntnu.nms.domainModel.PoolRegistry;
import no.ntnu.nms.licenseLedger.LicenseLedger;


public class Init {
    public static void main(String[] args) {
        PoolRegistry.init("data/test/pool_test.ser");
        LicenseLedger.init("data/test/ledger_test.txt");
    }
}
