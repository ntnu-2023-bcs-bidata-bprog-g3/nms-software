package nms.file_handler;

import no.ntnu.nms.file_handler.FileHandler;
import no.ntnu.nms.logging.Logging;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileHandlerTest {

    @BeforeAll
    public static void init() {
        try {
            Logging.setUpLogger("ALL");
        } catch (IOException ignore) {
            fail("Failed to initialize logger");
        }
    }

    @AfterEach
    public void tearDown() {
        try {
            Files.deleteIfExists(Path.of("test/test.txt"));
            Files.deleteIfExists(Path.of("test/test.txt.bak"));
            Files.deleteIfExists(Path.of("test"));
        } catch (IOException ignore) {}
    }

    @Test
    public void TestReadAdnWrite() {
        String test = "testString";
        FileHandler.writeToFile(SerializationUtils.serialize(test), "data/test/test.txt");
        String read = (String) SerializationUtils.deserialize(
                FileHandler.readFromFile("test/test.txt"));
        assertEquals(test, read);
    }

    @Test
    public void TestBackup() {
        String test = "testString";
        FileHandler.writeToFile(SerializationUtils.serialize(test), "data/test/test.txt");
        String read = (String) SerializationUtils.deserialize(
                FileHandler.readFromFile("test/test.txt"));
        assertEquals(test, read);

        try {
            FileHandler.backup("test/test.txt");
        } catch (IOException e) {
            fail("Failed to backup file");
        }
        read = (String) SerializationUtils.deserialize(
                FileHandler.readFromFile("test/test.txt.bak"));

        assertEquals(test, read);
    }
    @Test
    public void TestDeleteBackup() {
        String test = "testString";
        FileHandler.writeToFile(SerializationUtils.serialize(test), "data/test/test.txt");
        String read = (String) SerializationUtils.deserialize(
                FileHandler.readFromFile("test/test.txt"));
        assertEquals(test, read);

        try {
            FileHandler.backup("test/test.txt");
        } catch (IOException e) {
            fail("Failed to backup file");
        }
        read = (String) SerializationUtils.deserialize(
                FileHandler.readFromFile("test/test.txt.bak"));

        assertEquals(test, read);

        assertTrue(Files.exists(Path.of("test/test.txt.bak")));

        FileHandler.deleteBackup("test/test.txt");

        assertFalse(Files.exists(Path.of("test/test.txt.bak")));
    }
}
