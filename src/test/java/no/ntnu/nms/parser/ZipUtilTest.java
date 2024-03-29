package no.ntnu.nms.parser;

import no.ntnu.nms.Constants;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import no.ntnu.nms.exception.ParserException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtilTest {


    @AfterAll
    public static void tearDown() throws IOException{
        Path testDir = Path.of(Constants.TEST_FILES_PATH);
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
    public void TestUnzipperSuccess() throws IOException, ParserException {
        byte[] licenseJsonData = "{\"license\": \"test\"}".getBytes(StandardCharsets.UTF_8);
        byte[] licenseJsonSignatureData = "signature".getBytes(StandardCharsets.UTF_8);
        byte[] zipData = createZipFile("license.json", licenseJsonData, "license.json.signature", licenseJsonSignatureData);

        String extractedDirName = null;

        // Unzip the file and verify that the license and signature files were extracted
        try (ZipInputStream inputStream = new ZipInputStream(new ByteArrayInputStream(zipData))) {
            extractedDirName = ZipUtil.unzipper(inputStream);
            Path licenseJsonPath = Path.of("data/temp/" + extractedDirName + "/license.json");
            Path licenseJsonSignaturePath = Path.of("data/temp/" + extractedDirName + "/license.json.signature");
            Assertions.assertTrue(Files.exists(licenseJsonPath));
            Assertions.assertArrayEquals(licenseJsonData, Files.readAllBytes(licenseJsonPath));
            Assertions.assertTrue(Files.exists(licenseJsonSignaturePath));
            Assertions.assertArrayEquals(licenseJsonSignatureData, Files.readAllBytes(licenseJsonSignaturePath));
        } finally {
            // Clean up
            Path dir = Path.of("data/temp/" + extractedDirName);
            if (Files.exists(dir)) {
                try (var paths = Files.walk(dir)) {
                    paths.sorted(java.util.Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                } catch (IOException ignore) {}
            }
        }
    }

    private byte[] createZipFile(String filename1, byte[] data1, String filename2, byte[] data2) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipStream = new ZipOutputStream(outputStream)) {
            addToZipStream(zipStream, filename1, data1);
            addToZipStream(zipStream, filename2, data2);
        }
        return outputStream.toByteArray();
    }

    private byte[] createZipFile(String filename, byte[] data) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipStream = new ZipOutputStream(outputStream)) {
            addToZipStream(zipStream, filename, data);
        }
        return outputStream.toByteArray();
    }

    private void addToZipStream(ZipOutputStream zipStream, String filename, byte[] data) throws IOException {
        ZipEntry entry = new ZipEntry(filename);
        zipStream.putNextEntry(entry);
        zipStream.write(data);
        zipStream.closeEntry();
    }

    @Test
    public void TestUnzipperInvalidFile() throws IOException {
        // Create a zip file containing an invalid file
        byte[] invalidFileData = "invalid data".getBytes(StandardCharsets.UTF_8);
        byte[] zipData = createZipFile("invalid.txt", invalidFileData);

        // Unzip the file and verify that a ParserException is thrown
        try (ZipInputStream inputStream = new ZipInputStream(new ByteArrayInputStream(zipData))) {
            Assertions.assertThrows(ParserException.class, () -> ZipUtil.unzipper(inputStream));
        } finally {
            // Clean up
            Path dir = Path.of("data/temp/" + "invalid.txt");
            if (Files.exists(dir)) {
                try (var paths = Files.walk(dir)) {
                    paths.sorted(java.util.Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                } catch (IOException ignore) {}
            }
        }
    }

}
