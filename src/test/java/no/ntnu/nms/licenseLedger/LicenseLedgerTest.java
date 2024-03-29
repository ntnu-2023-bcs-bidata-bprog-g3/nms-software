package no.ntnu.nms.licenseLedger;

import no.ntnu.nms.logging.Logging;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static no.ntnu.nms.Constants.TEST_FILES_PATH;

public class LicenseLedgerTest {
    private static final String LEDGER_PATH = TEST_FILES_PATH + "ledger"+ File.separator +"test_ledger.txt";
    private static final String LICENSE_PATH = TEST_FILES_PATH + "licenses"+ File.separator +"test_license.txt";
    private static final String OTHER_LICENSE_PATH = TEST_FILES_PATH + "licenses"+ File.separator + "other_license.txt";

    @BeforeAll
    static void init() throws IOException {
        try {
            Logging.setUpLogger("ALL");
        } catch (IOException ignore) {}

        //check if the test directory exists
        Path testDir = Path.of(TEST_FILES_PATH);
        if (!Files.exists(testDir)) {
            Files.createDirectory(testDir);
        }
        //check if the ledger directory exists
        Path ledgerDir = Path.of(TEST_FILES_PATH + "ledger");
        if (!Files.exists(ledgerDir)) {
            Files.createDirectory(ledgerDir);
        }
        //check if the license directory exists
        Path licenseDir = Path.of(TEST_FILES_PATH + "licenses");
        if (!Files.exists(licenseDir)) {
            Files.createDirectory(licenseDir);
        }
        // Initialize the ledger with the test ledger path
        no.ntnu.nms.license.LicenseLedger.init(LEDGER_PATH);

        //create a test license file
        Files.createFile(new File(LICENSE_PATH).toPath());
        //write a test license to the file
        Files.writeString(new File(LICENSE_PATH).toPath(), "This is a test license");

        // create other test license file
        Files.createFile(new File(OTHER_LICENSE_PATH).toPath());
        //write a test license to the file
        Files.writeString(new File(OTHER_LICENSE_PATH).toPath(), "This is another test license");
    }

    @AfterAll
    public static void tearDown() throws IOException{
        Path testDir = Path.of(TEST_FILES_PATH);
        if (Files.exists(testDir)) {
            try (var paths = Files.walk(testDir)) {
                paths.sorted(java.util.Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException ignore) {}
        }
        Files.deleteIfExists(testDir);
    }

    @Test
    void testAddLicenseToLedger() {
        // Add a license to the ledger
        no.ntnu.nms.license.LicenseLedger.getInstance().addLicenseToLedger(LICENSE_PATH);
        // Verify that the license is in the ledger
        Assertions.assertTrue(no.ntnu.nms.license.LicenseLedger.getInstance().licenseIsInLedger(LICENSE_PATH));
    }

    @Test
    void testLicenseIsInLedger() {
        // Add a license to the ledger
        no.ntnu.nms.license.LicenseLedger.getInstance().addLicenseToLedger(LICENSE_PATH);
        // Verify that the license is in the ledger
        Assertions.assertTrue(no.ntnu.nms.license.LicenseLedger.getInstance().licenseIsInLedger(LICENSE_PATH));
        // Verify that a different license is not in the ledger
        Assertions.assertFalse(no.ntnu.nms.license.LicenseLedger.getInstance().licenseIsInLedger(OTHER_LICENSE_PATH));
    }
}