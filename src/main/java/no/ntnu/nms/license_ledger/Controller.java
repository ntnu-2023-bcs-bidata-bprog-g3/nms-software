import no.ntnu.nms.file_handler.FileHandler;
import no.ntnu.nms.security.Cryptography;
import no.ntnu.nms.security.KeyGenerator;
import org.springframework.util.SerializationUtils;

import java.nio.file.Files;
import java.nio.file.Path;

public class Controller {

    /**
     * The directory where the ledger and its checksum is stored.
     */
    private static final String LEDGER_DIR = "data/ledger/";


    /**
     * The name of the ledger file.
     */
    private static final String LEDGER_NAME = "ledger.txt";
    /**
     * The name of the ledger checksum file.
     */
    private static final String LEDGER_HASH_NAME = LEDGER_NAME + ".md5";


    /**
     * The complete path to the ledger file.
     */
    private static final String LEDGER_PATH = LEDGER_DIR + LEDGER_NAME;
    /**
     * The complete path to the ledger checksum file.
     */
    private static final String LEDGER_HASH_PATH = LEDGER_DIR + LEDGER_HASH_NAME;

    /**
     * Gets the ledger checksum.
     * @return {@link String} The ledger checksum, null if the ledger does not exist.
     */
    private static String getLedgerHash() {
        return (Files.notExists(Path.of(LEDGER_PATH))) ?  null : (String) SerializationUtils.deserialize(Cryptography.xorWithKey(FileHandler.readFromFile(LEDGER_PATH), KeyGenerator.KEY));
    }


}