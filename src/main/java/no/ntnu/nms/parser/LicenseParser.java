package no.ntnu.nms.parser;

import no.ntnu.nms.logging.Logging;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import no.ntnu.nms.exception.ParserException;

import java.io.*;
import java.nio.file.Files;
import java.util.zip.ZipInputStream;

public class LicenseParser {

    private static File licenseFile = new File("data/temp/zip/license.json");

    public LicenseParser(ZipInputStream inputStream) {
        String dirName = null;
        try {
            dirName = ZipUtil.unzipper(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assignFiles(dirName);
    }

    public void assignFiles(String dirName) {
        this.licenseFile = new File("data/temp/" + dirName + "/license.json");
    }

    public static void parse() {
        JSONObject object = null;
        try {
            object = new JSONObject(new String(Files.readAllBytes(licenseFile.toPath())));
        } catch (IOException ignore) {}
        if (false) {
            throw new ParserException("The license is not yet verified");
        }
        try {
            JSONArray keyList = (JSONArray) object.getJSONObject("licence").get("keys");
            for (Object o : keyList) {
                System.out.println(o);
            }
        } catch (JSONException | NullPointerException e) {
            throw new ParserException("There was an error reading the license file" + e.getMessage());
        }
    }

    /**
     * Verify the signature of the license file
     * @return {@link Boolean} true if the signature is valid, false otherwise
     */
    public static boolean verifyFiles(){
        String command = "openssl dgst -sha256 -verify root-pubkey.pem -signature license.json.signature license.json"; // Replace with your desired command
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(new File("data/temp/zip/"));
            //Assume Linux
            processBuilder.command("bash", "-c", command);
            Process process = processBuilder.start();
            return process.waitFor() == 0;
        } catch (Exception e) {
            Logging.getLogger().warning("Error while verifying files" + e.getMessage());
        }
        return false;
    }
}
