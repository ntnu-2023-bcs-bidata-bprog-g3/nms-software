package no.ntnu.nms.persistence;

import static no.ntnu.nms.Constants.TEST_FILES_PATH;
import static org.junit.jupiter.api.Assertions.*;

import no.ntnu.nms.Constants;
import no.ntnu.nms.license.LicenseLedger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import no.ntnu.nms.domainmodel.PoolRegistry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class PersistenceControllerTest {

    private static final String TEST_DIR = Constants.TEST_FILES_PATH + "persistenceController/";

    private static final String filePath = TEST_DIR + "poolregistry.ser";
    private static final String checksumPath = filePath + ".md5";

    @BeforeAll
    public static void init() {
        PoolRegistry.init(filePath);
        try {
            Files.deleteIfExists(Path.of(checksumPath));
        } catch (IOException ignore) {}
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
    public void TestSaveToFile() throws IOException{
        // Create a new pool registry and save it to a file
        PoolRegistry poolRegistry = PoolRegistry.getInstance(true);
        // Save the instance to file
        PersistenceController.saveToFile(poolRegistry, filePath, true);

        // Check that the file and checksum file have been created
        File file = new File(filePath);
        File checksumFile = new File(filePath + ".md5");

        assertTrue(file.exists());
        assertTrue(checksumFile.exists());

        // Delete the files
        Files.deleteIfExists(Paths.get(filePath));
        Files.deleteIfExists(Paths.get(filePath + ".md5"));
    }

    @Test
    public void TestLoadFromFile() throws Exception {
        // Create an instance of PoolRegistry
        PoolRegistry expectedPoolRegistry = PoolRegistry.getInstance(true);

        // Save the instance to file
        PersistenceController.saveToFile(expectedPoolRegistry, filePath, true);

        // Load the instance from file
        PersistenceController.loadFromFile(filePath);

        // Check that the PoolRegistry instance has been loaded correctly
        PoolRegistry actualPoolRegistry = PoolRegistry.getInstance(true);
        assertNotNull(actualPoolRegistry);
        assertInstanceOf(PoolRegistry.class, actualPoolRegistry);

        // Delete the file
        Files.deleteIfExists(Path.of(filePath));
    }

    @Test
    public void testSaveToFileWithChecksum() throws IOException {
        // Create a new object to serialize
        List<Integer> list = Arrays.asList(1, 2, 3, 4);

        // Save the object to a file with checksum
        PersistenceController.saveToFile(list, filePath, true);

        // Check that the file and checksum file exist
        assertTrue(new File(filePath).exists());
        assertTrue(new File(checksumPath).exists());

        // Clean up
        Files.deleteIfExists(Path.of(filePath));
        Files.deleteIfExists(Path.of(checksumPath));
    }

    @Test
    public void testSaveToFileWithoutChecksum() throws IOException {
        // Create a new object to serialize
        List<Integer> list = Arrays.asList(1, 2, 3, 4);

        // Save the object to a file without checksum
        PersistenceController.saveToFile(list, filePath, false);

        // Check that the file exists and the checksum file does not exist
        assertTrue(new File(filePath).exists());
        assertFalse(new File(checksumPath).exists());

        // Clean up
        Files.deleteIfExists(Path.of(filePath));
        Files.deleteIfExists(Path.of(checksumPath));
    }

    @Test
    public void testAppendToFile() throws Exception {
        LicenseLedger.init(TEST_DIR + "licenseledger.txt");
        // Create a test string to append to the file
        String toAppend = "Test line";

        // Append the string to the file
        PersistenceController.appendToFile(toAppend, TEST_DIR + "licenseledger.txt");

        // Check that the file has been appended correctly
        String fileContent = PersistenceController.loadLedger(TEST_DIR + "licenseledger.txt");
        assertTrue(fileContent.endsWith(toAppend));

        // Delete the file
        Files.deleteIfExists(Paths.get(TEST_DIR + "licenseledger.txt"));
    }

    @Test
    public void testLoadLedger() throws Exception {
        LicenseLedger.init(TEST_DIR + "licenseledger.txt");
        Path licenseLedgerPath = Paths.get(TEST_DIR + "licenseledger.txt");

        // Load the file content and check that it matches the test string
        String fileContent = PersistenceController.loadLedger(licenseLedgerPath.toString());
        assertEquals("", fileContent); // will be empty

        // Delete the file
        Files.deleteIfExists(licenseLedgerPath);
    }
}
