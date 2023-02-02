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
    public static boolean savePoolReg(PoolRegistry poolreg) {
        byte[] encrypted = Cryptography.encrypt(poolreg, KeyGenerator.generateKey());
        return FileHandler.writeToFile(encrypted);
    }

    /**
     * Loads a PoolRegistry object from file.
     * @return The PoolRegistry object loaded from file.
     */
    public static PoolRegistry loadPoolReg() {
        byte[] encrypted = FileHandler.readFromFile();
        return Cryptography.decrypt(encrypted, KeyGenerator.generateKey());
    }

}
