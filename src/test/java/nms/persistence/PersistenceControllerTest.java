package nms.persistence;

import static org.junit.jupiter.api.Assertions.*;

import nms.Constants;
import no.ntnu.nms.licenseLedger.LicenseLedger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import no.ntnu.nms.domainModel.PoolRegistry;
import no.ntnu.nms.persistence.PersistenceController;

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
    }

    @AfterAll
    public static void tearDown() throws IOException{
        Path testDir = Path.of(TEST_DIR);
        if (Files.exists(testDir)) {
            Files.walk(testDir).sorted(java.util.Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
        Files.deleteIfExists(testDir);
    }

    @Test
    public void TestSaveToFile() throws IOException{
        // Create a new pool registry and save it to a file
        PoolRegistry poolRegistry = PoolRegistry.getInstance();
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
        PoolRegistry expectedPoolRegistry = PoolRegistry.getInstance();

        // Save the instance to file
        PersistenceController.saveToFile(expectedPoolRegistry, filePath, true);

        // Load the instance from file
        PersistenceController.loadFromFile(filePath);

        // Check that the PoolRegistry instance has been loaded correctly
        PoolRegistry actualPoolRegistry = PoolRegistry.getInstance();
        assertNotNull(actualPoolRegistry);
        assertInstanceOf(PoolRegistry.class, actualPoolRegistry);

        // Delete the file
        Files.deleteIfExists(Paths.get(filePath));
    }

    @Test
    public void testSaveToFileWithChecksum() {
        // Create a new object to serialize
        List<Integer> list = Arrays.asList(1, 2, 3, 4);

        // Save the object to a file with checksum
        PersistenceController.saveToFile(list, filePath, true);

        // Check that the file and checksum file exist
        assertTrue(new File(filePath).exists());
        assertTrue(new File(checksumPath).exists());

        // Clean up
        try {
            Files.delete(Path.of(filePath));
            Files.delete(Path.of(checksumPath));

        } catch (IOException ignore) {}
    }

    @Test
    public void testSaveToFileWithoutChecksum()  {
        // Create a new object to serialize
        List<Integer> list = Arrays.asList(1, 2, 3, 4);

        // Save the object to a file without checksum
        PersistenceController.saveToFile(list, filePath, false);

        // Check that the file exists and the checksum file does not exist
        assertTrue(new File(filePath).exists());
        assertFalse(new File(checksumPath).exists());

        // Clean up
        try {
            Files.delete(Path.of(filePath));
            Files.delete(Path.of(checksumPath));

        } catch (IOException ignore) {}
    }
}
