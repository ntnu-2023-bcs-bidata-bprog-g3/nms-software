package no.ntnu.nms;

import no.ntnu.nms.domainModel.PoolRegistry;
import no.ntnu.nms.persistence.Controller;

public class Init {

    public static void main(String[] args) {
        PoolRegistry.instance = new PoolRegistry();
        Controller.savePoolRegAndChecksum();
    }
}
