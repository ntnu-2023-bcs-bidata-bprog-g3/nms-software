package no.ntnu.nms.parser;

import no.ntnu.nms.logging.Logging;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.*;

/**
 * ZipUtil is a utility class for unzipping files and handling the content of a top-level license.
 */
public class ZipUtil {

    /**
     * Unzip a zip file and return a message if the file is not valid. Handles
     * the license file and signature file from the Zip file.
     *
     * @param inputStream {@link ZipFile} the {@link ZipInputStream} of the zip file to unzip
     */
    public static String unzipper(ZipInputStream inputStream) throws IOException {
        String dirName = String.valueOf(Math.random() * 1000000000000000000L);
        Path path = Paths.get("data/temp/" + dirName);
        for (ZipEntry entry; (entry = inputStream.getNextEntry()) != null; ) {
            Path resolvedPath = path.resolve(entry.getName());
            if (entry.isDirectory()) {
                throw new IOException("ZipFile contains directories");
            }

            switch (entry.getName()) {
                case "license.json":
                    Logging.getLogger().info("Found license.json");
                    break;
                case "license.json.signature":
                    Logging.getLogger().info("Found license.json.signature");
                    break;
                case "public_key.pem":
                    Logging.getLogger().info("Found public_key.pem");
                    break;
                default:
                    continue;
            }

            Files.createDirectories(resolvedPath.getParent());
            Files.copy(inputStream, resolvedPath);
        }
        return dirName;
    }
}
