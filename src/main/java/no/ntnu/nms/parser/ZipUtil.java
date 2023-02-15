package no.ntnu.nms.parser;

import no.ntnu.nms.exception.ParserException;
import no.ntnu.nms.logging.Logging;

import java.io.*;
import java.util.zip.*;

/**
 * ZipUtil is a utility class for unzipping files and handling the content of a top-level license.
 */
public class ZipUtil {

    /**
     * Unzip a zip file and return a message if the file is not valid. Handles
     * the license file and signature file from the Zip file.
     * @param inputStream {@link ZipFile} the {@link ZipInputStream} of the zip file to unzip.
     * @return File[] an array of the license file, signature file and public key file.
     */
    public static File[] unzipper(ZipInputStream inputStream) {

        File licenseFile = null;
        File signatureFile = null;
        File publicKeyFile = null;

        try {
            for (ZipEntry entry; (entry = inputStream.getNextEntry()) != null; ) {
                if (entry.isDirectory()) {
                    throw new IOException("Zip file contains a directory");
                }
                switch (entry.getName()) {
                    case "license.json":
                        licenseFile = new File(entry.getName());
                        //Files.copy(inputStream, licenseFile.toPath());
                        break;
                    case "license_sign.json":
                        signatureFile = new File(entry.getName());
                        //Files.copy(inputStream, signatureFile.toPath());
                        break;
                    case "public_key.pem":
                        publicKeyFile = new File(entry.getName());
                        //Files.copy(inputStream, publicKeyFile.toPath());
                        break;
                    default:
                        throw new IOException("Zip file contains an unknown file: " + entry.getName());
                }
            }
            if (licenseFile == null || signatureFile == null || publicKeyFile == null) {
                throw new IOException("Zip file is missing a file");
            }
        } catch (IOException e) {
            Logging.getLogger().severe("Failed to unzip the file" + e.getMessage());
            throw new ParserException("Failed to unzip the file: " + e.getMessage());
        }
        return new File[]{licenseFile, signatureFile, publicKeyFile};
    }
}
