package no.ntnu.nms.parser;

import java.util.zip.*;

/**
 * ZipUtil is a utility class for unzipping files and handling the content of a top-level license.
 */
public class ZipUtil {

    /**
     * The name of the license file.
     */
    private static final String LICENSE_FILE = "license.json";
    /**
     * The name of the signature file.
     */
    private static final String SIGNATURE = LICENSE_FILE + ".signature";


    /**
     * Unzip a zip file and return a message if the file is not valid. Handles
     * the license file and signature file from the Zip file.
     * @param zipFile {@link ZipFile} the zip file to unzip.
     * @return {@link String} a message if the file is not valid.
     */
    public String unzipper(ZipFile zipFile) {
        ZipEntry licenseEntry;
        ZipEntry signatureEntry;

        try {
            licenseEntry = zipFile.getEntry(LICENSE_FILE);
            signatureEntry = zipFile.getEntry(SIGNATURE);
        } catch (IllegalStateException ignored) {
            return "Failed to unzip file";
        }

        if (licenseEntry == null || signatureEntry == null) {
            return "Zip file does not contain necessary files";
        }

        // TODO: Add functionality for parsing the license file and signature file

        return null;
    }

}
