package no.ntnu.nms.parser;

import no.ntnu.nms.domainModel.Pool;
import no.ntnu.nms.domainModel.PoolRegistry;
import no.ntnu.nms.licenseLedger.LicenseLedger;
import no.ntnu.nms.logging.Logging;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import no.ntnu.nms.exception.ParserException;

import java.io.*;
import java.nio.file.Files;
import java.util.zip.ZipInputStream;

public class LicenseParser {

    private File licenseFile;
    private File signatureFile;
    private File publicKeyFile;

    private String dirName;

    private final String publicKey = "-----BEGIN PUBLIC KEY-----\n" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvP769XecV3Z0E2BC19Vo\n" +
            "wu4z4OkVxjnfXFZiqLwD686W69vRDjn1ZCUphLeSB/x/5bhkiJHSQSJVDUgYBhzo\n" +
            "i2itFpEIrweuaq1sSiQjRcnv+UV9XD7BU1JmR4zZQb8xyrbQeXsKhN6gT3Q0/nmt\n" +
            "j4PF0fiRKeuA8pW618ftbjraSY/cgr8C/x3A1GcJBMvKMVGgZrQSTxZViZN0WdcP\n" +
            "p+jG70s4G5jL35B8NdlafiW7pYaE4A8VUHcHIvdrkJWPNOwiTPa2CPRtopjZ1b83\n" +
            "uPkDxVUSjMKPNkcDyXI4w/n4JwUNdWrgLHvTCeMOJWQheOM22+RuiEqA+z2fnvS2\n" +
            "0QIDAQAB\n" +
            "-----END PUBLIC KEY-----\n";

    public LicenseParser() {
        licenseFile = null;
        signatureFile = null;
        publicKeyFile = null;
        dirName = null;
    }

    private void assignFiles(String dirName) {
        this.licenseFile = new File("data/temp/" + dirName + "/license.json");
        this.signatureFile = new File("data/temp/" + dirName + "/license.json.signature");
        this.publicKeyFile = new File("data/temp/" + dirName + "/root-pubkey.pem");
    }

    public void parse(ZipInputStream inputStream) throws ParserException {
        try {
            this.dirName = ZipUtil.unzipper(inputStream);
        } catch (IOException e) {
            throw new ParserException("Failed to unzip files: " + e.getMessage());
        } catch (ParserException e) {
            deleteFiles();
            throw e;
        }

        assignFiles(dirName);

        verifyFiles();

        if (LicenseLedger.getInstance().licenseIsInLedger(licenseFile.getPath())) {
            deleteFiles();
            throw new ParserException("This license is already in use");
        }

        try {
            parseFiles();
        } catch (ParserException e) {
            deleteFiles();
            throw new ParserException("Failed to parse files: " + e.getMessage());
        }
        deleteFiles();
    }


    private void parseFiles() {
        JSONObject object = null;

        try {
            object = new JSONObject(new String(Files.readAllBytes(licenseFile.toPath())));
        } catch (IOException ignore) {}

        if (object == null) {
            throw new ParserException("The license file is empty");
        }

        try {
            JSONArray keyList = (JSONArray) object.getJSONObject("licence").get("keys");
            for (Object o : keyList) {
                parseLicense((JSONObject) o);
            }
        } catch (JSONException | NullPointerException e) {
            throw new ParserException("There was an error reading the license file" + e.getMessage());
        }
    }

    private void parseLicense(JSONObject object) throws ParserException {
        String name;
        int duration;
        String description;
        try {
            name = (String) object.get("name");
            String durationString = (String) object.get("duration");
            String durationCleanString = durationString.replaceAll("[^0-9]", "");
            if (durationCleanString.isEmpty()) return;
            duration = Integer.parseInt(durationCleanString)*60;
            description = (String) object.getJSONObject("info").get("description");
        } catch (JSONException e) {
            throw new ParserException("There was an error reading the license file" + e.getMessage());
        }
        LicenseLedger.getInstance().addLicenseToLedger(licenseFile.getPath());
        Pool pool = PoolRegistry.getInstance().getPoolByMediaFunction(name);
        if (pool == null) {
            PoolRegistry.getInstance().addPool(new Pool(name, duration, description));
        } else {
            if (!PoolRegistry.getInstance().getPoolByMediaFunction(name).addSeconds(duration)) {
                throw new ParserException("Could not add seconds to pool");
            }
        }
    }

    /**
     * Verify the signature of the license file
     */
    private void verifyFiles() throws ParserException {
        if (!licenseFile.exists() || !signatureFile.exists() || !publicKeyFile.exists()) {
            throw new ParserException("Could not find all files");
        }

        try {
            // Create a temporary file with the contents of the publicKey string
            File publicKeyTempFile = File.createTempFile("publicKey", ".pem");
            BufferedWriter writer = new BufferedWriter(new FileWriter(publicKeyTempFile));
            writer.write(publicKey);
            writer.close();

            String command = String.format("openssl dgst -sha256 -verify %s -signature %s %s",
                    publicKeyTempFile.getAbsolutePath(),
                    signatureFile.getAbsolutePath(),
                    licenseFile.getAbsolutePath());

            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(new File("data/temp/" + dirName));
            // Assume Unix
            processBuilder.command("sh", "-c", command);
            Process process = processBuilder.start();
            if (process.waitFor() != 0) {
                throw new ParserException("Could not verify license");
            }

            // Delete the temporary file
            publicKeyTempFile.delete();
        } catch (Exception e) {
            throw new ParserException("Error while verifying files: " + e.getMessage());
        }
    }

    private void deleteFiles() {
        try {
            FileUtils.deleteDirectory(new File("data/temp/" + dirName));
        } catch (IOException e) {
            Logging.getLogger().warning("Could not delete files in folder: " + dirName);
        }
    }
}
