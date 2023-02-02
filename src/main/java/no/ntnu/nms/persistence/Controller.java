package no.ntnu.nms.persistence;

import no.ntnu.nms.domain_model.PoolRegistry;
import no.ntnu.nms.security.Cryptography;
import no.ntnu.nms.security.KeyGenerator;

public class Controller {

    /**
     * Saves a PoolRegistry object to file.
     * @param poolreg The PoolRegistry object to save.
     * @return True if the PoolRegistry object was saved successfully, false otherwise.
     */
    public boolean savePoolReg(PoolRegistry poolreg) {
        byte[] encrypted = Cryptography.encrypt(poolreg, KeyGenerator.generateKey());
        return FileHandler.writeToFile(encrypted, "poolreg.ser");
    }

    /**
     * Loads a PoolRegistry object from file.
     * @return The PoolRegistry object loaded from file.
     */
    public PoolRegistry loadPoolReg() {
        byte[] encrypted = FileHandler.readFromFile("poolreg.ser");
        return Cryptography.decrypt(encrypted, KeyGenerator.generateKey());
    }

}
