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

        String command = "openssl dgst -sha256 -verify root-pubkey.pem -signature license.json.signature license.json";
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(new File("data/temp/" + dirName));
            //Assume Unix
            processBuilder.command("sh", "-c", command);
            Process process = processBuilder.start();
            if (process.waitFor() != 0) throw new ParserException("Could not verify license");
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
