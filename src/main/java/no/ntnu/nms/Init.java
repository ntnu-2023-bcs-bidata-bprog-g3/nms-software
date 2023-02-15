package no.ntnu.nms;

import no.ntnu.nms.domainModel.PoolRegistry;
import no.ntnu.nms.file_handler.FileHandler;
import no.ntnu.nms.licenseLedger.LicenseLedger;
import no.ntnu.nms.security.Cryptography;
import no.ntnu.nms.security.KeyGenerator;
import org.springframework.util.SerializationUtils;

public class Init {

    public static void main(String[] args) {
        PoolRegistry.init();
        byte[] newLedger = Cryptography.xorWithKey(SerializationUtils.serialize("this is a test"), KeyGenerator.KEY);
        FileHandler.writeToFile(newLedger, "data/temp/license.txt");
        LicenseLedger.init();
    }
}
