package no.ntnu.nms.parser;

import no.ntnu.nms.exception.ParserException;

import java.io.File;
import java.util.zip.ZipInputStream;

public class LicenseParser {

    private final File licenseFile;
    private final File signatureFile;
    private final File publicKeyFile;

    public LicenseParser(ZipInputStream inputStream) throws ParserException {
        File[] files = ZipUtil.unzipper(inputStream);
        licenseFile = files[0];
        signatureFile = files[1];
        publicKeyFile = files[2];

        if (licenseFile == null || signatureFile == null || publicKeyFile == null) {
            throw new ParserException("The Zip file does not contain the required files");
        }
    }

    public void parse() {
        // TODO: Implement parsing of the files, calls to checks, adding the license to ledger and similar ...
        System.out.println("True");
    }
}
