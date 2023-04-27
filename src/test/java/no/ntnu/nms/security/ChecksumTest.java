package no.ntnu.nms.security;

import no.ntnu.nms.exception.CryptographyException;
import no.ntnu.nms.exception.FileHandlerException;
import no.ntnu.nms.filehandler.FileHandler;
import no.ntnu.nms.logging.Logging;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;

public class ChecksumTest {

    private File file, file2;

    @BeforeAll
    public static void setUpAll(){
        try {
            Logging.setUpLogger("ALL");
        } catch (IOException ignore) {}
    }
    @BeforeEach
    public void setUp(){
        file = null;
        file2 = null;
        try {
            file = File.createTempFile("temp", null);
            file.deleteOnExit();
            file2 = File.createTempFile("temp", null);
            file2.deleteOnExit();
        } catch (IOException ignore){}
    }

    @Test
    public void TestGenerateChecksumFromFile() {
        if (file != null) {
            try {
                String checksum = Checksum.generateFromFile(file.getAbsolutePath());
                assertNotNull(checksum);
            } catch (CryptographyException e) {
                fail();
            }
        }
    }

    // Tests are currently broken, but we dont have time to fix them.

    @Test
    public void TestCompareChecksum(){
        if (file != null && file2 != null) {
            byte[] checksumBytes = Cryptography.xorWithKey(Checksum.generateFromFile(file.getAbsolutePath()).getBytes(), KeyGenerator.KEY);
            FileHandler.writeToFile(checksumBytes, file2.getAbsolutePath());

            boolean result = Checksum.compare(file.getAbsolutePath(), file2.getAbsolutePath());
            assertTrue(result);
        }
    }

    @Test
    public void TestCompareChecksumNegative(){
        if (file != null && file2 != null) {
            byte[] checksumBytes = Cryptography.xorWithKey("this is a test checksum".getBytes(), KeyGenerator.KEY);
            FileHandler.writeToFile(checksumBytes, file2.getPath());

            boolean result = Checksum.compare(file.getPath(), file2.getPath());
            assertFalse(result);
        }
    }

    @Test
    public void TestCompareChecksumIllegal(){
        if (file != null && file2 != null) {
            assertThrows(FileHandlerException.class, () -> Checksum.compare(file.getAbsolutePath(), file2.getAbsolutePath()));
        }
    }

    @Test
    public void TestCompareChecksumIllegal2(){
        if (file2 != null) {
            assertThrows(CryptographyException.class, () -> Checksum.compare("test", file2.getAbsolutePath()));
        }
    }
}
