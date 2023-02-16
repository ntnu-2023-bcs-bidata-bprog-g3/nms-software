package no.ntnu.nms.parser;

import no.ntnu.nms.exception.ParserException;
import no.ntnu.nms.logging.Logging;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
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
    public static String unzipper(ZipInputStream inputStream) throws ParserException, IOException {
        String dirName = String.valueOf(Math.abs(((int) Math.pow(10, 8))
                + (new Random().nextInt(9 * (int) Math.pow(10, 8)))));
        System.out.println("Dirname: " + dirName);
        final Path dir = Path.of("data/temp/" + dirName);
        for (ZipEntry entry; (entry = inputStream.getNextEntry()) != null; ) {
            try {
                if (entry.isDirectory() || entry.getName().contains("/")) {
                    continue; //ignore the directories in the zipfile
                }
                Path resolvedPath = dir.resolve(entry.getName());
                switch (entry.getName()) {
                    case "license.json":
                        Logging.getLogger().info("Found license.json");
                        break;
                    case "license.json.signature":
                        Logging.getLogger().info("Found license.json.signature");
                        break;
                    case "root-pubkey.pem":
                        Logging.getLogger().info("Found public_key.pem");
                        break;
                    default:
                        throw new ParserException("Invalid file in zip file: " + entry.getName());
                }
                Files.createDirectories(resolvedPath.getParent());
                Files.copy(inputStream, resolvedPath);
            } catch (ParserException e) {
                throw new ParserException("Failed to unzip files: " + e.getMessage());
            }
        }
        return dirName;
    }
}
