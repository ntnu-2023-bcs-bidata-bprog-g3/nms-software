package nms.parser;

import no.ntnu.nms.exception.ParserException;
import no.ntnu.nms.licenseLedger.LicenseLedger;
import no.ntnu.nms.parser.LicenseParser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.text.html.parser.Parser;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static nms.Constants.TEST_FILES_PATH;
import static org.junit.jupiter.api.Assertions.*;

public class LicenseParserTest {

    private LicenseParser parser;

    @BeforeEach
    void setUp() {
        parser = new LicenseParser();
    }

    @AfterAll
    public static void tearDown() throws IOException{
        Path testDir = Path.of(TEST_FILES_PATH);
        if (Files.exists(testDir)) {
            Files.walk(testDir).sorted(java.util.Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
        Files.deleteIfExists(testDir);
    }

    @Test
    void TestParseInvalidFile(@TempDir File tempDir) throws Exception {
        // Create a valid license file
        File licenseFile = new File(tempDir, "license.json");
        FileWriter writer = new FileWriter(licenseFile);
        writer.write("{\"licence\": {\"keys\": [{\"name\": \"test\", \"duration\": \"1 day\", \"info\": {\"description\": \"test description\"}}]}}");
        writer.close();

        // Create a signature file
        File signatureFile = new File(tempDir, "license.json.signature");
        writer = new FileWriter(signatureFile);
        writer.write("signature");
        writer.close();

        // Create a zip input stream containing the license files
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(bos);
        zos.putNextEntry(new ZipEntry("license.json"));
        Files.copy(licenseFile.toPath(), zos);
        zos.putNextEntry(new ZipEntry("license.json.signature"));
        Files.copy(signatureFile.toPath(), zos);
        zos.closeEntry();
        zos.close();

        assertThrows(ParserException.class, () -> parser.parse(new ZipInputStream(new ByteArrayInputStream(bos.toByteArray()))));
    }
}
