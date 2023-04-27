package no.ntnu.nms.parser;

import no.ntnu.nms.domainmodel.PoolRegistry;
import no.ntnu.nms.exception.ParserException;
import no.ntnu.nms.license.LicenseLedger;
import no.ntnu.nms.logging.Logging;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static no.ntnu.nms.Constants.TEST_FILES_PATH;
import static org.junit.jupiter.api.Assertions.*;

public class LicenseParserTest {

    private LicenseParser parser;

    @BeforeAll
    public static void setUpAll(){
        LicenseLedger.init("test_files/persistenceController/licenseledger.txt");
        PoolRegistry.init("test_files/persistenceController/poolregistry.txt");
        try {
            Logging.setUpLogger("ALL");
        } catch (IOException ignore) {}
    }

    @BeforeEach
    public void setUp() {
        parser = new LicenseParser();
    }

    @AfterAll
    public static void tearDown() throws IOException {
        Path testDir = Path.of(TEST_FILES_PATH);
        if (Files.exists(testDir)) {
            try (var paths = Files.walk(testDir)) {
                paths.sorted(java.util.Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException ignore) {}
        }
        Files.deleteIfExists(testDir);

        Path dir = Path.of("data/temp/");
        //delete all files in the directory, but not the directory itself
        if (Files.exists(dir)) {
            try (var paths = Files.walk(dir)) {
                paths.sorted(java.util.Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException ignore) {}
        }
    }

    @Test
    void TestParseInvalidFile() throws Exception {
        // Create a valid license file
        File licenseFile = new File("data/temp/", "license.json");
        FileWriter writer = new FileWriter(licenseFile);
        writer.write("{\"licence\": {\"keys\": [{\"name\": \"test\", \"duration\": \"1 day\", \"info\": {\"description\": \"test description\"}}]}}");
        writer.close();

        // Create a signature file
        File signatureFile = new File("data/temp/", "license.json.signature");
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

        // Delete the license files
        licenseFile.delete();
        signatureFile.delete();
    }


    @Test
    void TestParseValidFiles() {
        //create ZipInputStream of data/temp/zip/files.zip
        ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(new FileInputStream("src/main/resources/test/file.zip"));
        } catch (FileNotFoundException e) {
            System.out.println("File not found" + e.getMessage());
            return; //ignore test if file is not found
        }

        try {
            parser.parse(zis);
        } catch (ParserException e) {
            fail();
        }
    }

    @Test
    void TestParseInvalidFiles() {
        //create ZipInputStream of data/temp/zip/files.zip
        ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(
                    new FileInputStream("src/main/resources/test/fake_license.zip"));
        } catch (FileNotFoundException e) {
            System.out.println("File not found" + e.getMessage());
            return; //ignore test if file is not found
        }

        ParserException exception = null;
        try {
            parser.parse(zis);
        } catch (ParserException e) {
            exception = e;
        }

        assertNotNull(exception);
        assertEquals("Error while verifying files: License file signature does not " +
                "correspond with the public key", exception.getMessage());
    }
}