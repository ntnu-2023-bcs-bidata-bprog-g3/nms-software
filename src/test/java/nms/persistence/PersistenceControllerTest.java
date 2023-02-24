package nms.persistence;

import static org.junit.jupiter.api.Assertions.*;

import nms.Constants;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import no.ntnu.nms.domainModel.PoolRegistry;
import no.ntnu.nms.persistence.PersistenceController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    public static void deInit() {
        try {
            Files.deleteIfExists(Path.of(filePath));
            Files.deleteIfExists(Path.of(filePath + ".md5"));
            Files.deleteIfExists(Path.of(TEST_DIR));
        } catch (IOException ignore) {}
    }

    @Test
    public void testSaveAndLoadPoolRegistry() {
        // Create a new pool registry and save it to a file
        PoolRegistry poolRegistry = PoolRegistry.getInstance();
        PersistenceController.saveToFile(poolRegistry, filePath, true);

        // Load the pool registry from the file and check that it matches the original
        PersistenceController.loadFromFile(filePath);
        assertEquals(poolRegistry.getPoolCount(),
                PoolRegistry.getInstance().getPoolCount());
        // Clean up
        try {
            Files.delete(Path.of(filePath));
            Files.delete(Path.of(checksumPath));
        } catch (IOException ignore) {}
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
