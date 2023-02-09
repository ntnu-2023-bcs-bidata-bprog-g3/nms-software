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

}