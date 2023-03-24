package no.ntnu.nms.parser;

import no.ntnu.nms.CustomerConstants;
import no.ntnu.nms.domainModel.Pool;
import no.ntnu.nms.domainModel.PoolRegistry;
import no.ntnu.nms.license.LicenseLedger;
import no.ntnu.nms.logging.Logging;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import no.ntnu.nms.exception.ParserException;

import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.zip.ZipInputStream;

/**
 * LicenseParser is a parser for the license file, with the corresponding checks and verifications.
 */
public class LicenseParser {

    private File licenseFile;
    private File signatureFile;

    private byte[] licenseContent;

    private String dirName;

    /**
     * Constructor for LicenseParser.
     */
    public LicenseParser() {
        licenseFile = null;
        licenseContent = null;
        signatureFile = null;
        dirName = null;
    }

    /**
     * Verify the license file and signature file.
     * @param dirName the name of the directory containing the files.
     */
    private void assignFiles(String dirName) {
        this.licenseFile = new File("data/temp/" + dirName + "/license.json");
        this.signatureFile = new File("data/temp/" + dirName + "/license.json.signature");
        try {
            this.licenseContent = Files.readAllBytes(licenseFile.toPath());
        } catch (IOException e) {
            throw new ParserException("Failed to read license file: " + e.getMessage());
        }
    }

    /**
     * Parse the license file and add the license to the ledger.
     * @param inputStream the input stream containing the license file and signature file.
     * @throws ParserException if the license file or signature file is invalid or checks fail.
     */
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


    /**
     * Parses the files.
     */
    private void parseFiles() {
        JSONObject object = new JSONObject(new String(this.licenseContent));

        if (object.isEmpty()) {
            throw new ParserException("The license file is empty");
        }

        try {
            JSONArray keyList = object.getJSONObject("license").getJSONArray("keys");
            for (int i = 0; i < keyList.length(); i++) {
                JSONObject keyObject = keyList.getJSONObject(i);
                parseLicense(keyObject);
            }
        } catch (JSONException | NullPointerException e) {
            throw new ParserException("Could not read license file: " + e.getMessage());
        }
    }

    /**
     * Parses the license file itself.
     * @param object the JSONObject contained in the license file
     * @throws ParserException if the license file is invalid
     */
    private void parseLicense(JSONObject object) throws ParserException {
        String name;
        int duration;
        String description;
        try {
            name = (String) object.get("name");
            duration = object.getInt("duration") * 60;
            description = (String) object.get("description");
        } catch (JSONException e) {
            throw new ParserException("Could not read: " + e.getMessage());
        }
        LicenseLedger.getInstance().addLicenseToLedger(licenseFile.getPath());
        Pool pool = PoolRegistry.getInstance(false).getPoolByMediaFunction(name);
        if (pool == null) {
            PoolRegistry.getInstance(false).addPool(new Pool(name, duration, description));
        } else {
            if (!pool.addSeconds(duration)) {
                throw new ParserException("Could not add seconds to pool");
            }
        }
    }

    /**
     * Verify the signature of the license file.
     */
    private void verifyFiles() throws ParserException {

        if (!licenseFile.exists() || !signatureFile.exists()) {
            throw new ParserException("Could not find all files");
        }

        try {
            byte[] signatureBytes = Files.readAllBytes(signatureFile.toPath());
            byte[] fileToVerifyBytes = Files.readAllBytes(licenseFile.toPath());

            if (!isSignatureValid(getPublicKey(), signatureBytes,
                    fileToVerifyBytes)) {
                throw new ParserException("License file signature does not " +
                        "correspond with the public key");
            }
        } catch (Exception e) {
            throw new ParserException("Error while verifying files: " + e.getMessage());
        }
    }

    /**
     * Get the public key from a string.
     * @return the public key.
     * @throws NoSuchAlgorithmException if the algorithm is not supported.
     * @throws InvalidKeySpecException if the key is invalid.
     */
    private PublicKey getPublicKey()
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String formattedPubKey = CustomerConstants.ROOT_PUBLIC_KEY
                .replace("-----BEGIN PUBLIC KEY-----\n", "")
                .replace("-----END PUBLIC KEY-----\n", "")
                .replaceAll("\\s+", "");

        byte[] byteKey = Base64.getDecoder().decode(formattedPubKey);

        X509EncodedKeySpec x509publicKey = new X509EncodedKeySpec(byteKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePublic(x509publicKey);
    }

    /**
     * Check if the signature is valid.
     * @param publicKey the public key to verify the signature with.
     * @param signatureBytes the signature to verify.
     * @param dataBytes the data to verify the signature with.
     * @return true if the signature is valid, false otherwise.
     * @throws NoSuchAlgorithmException if the algorithm is not supported.
     * @throws InvalidKeyException if the key is invalid.
     * @throws SignatureException if the signature is invalid.
     */
    private boolean isSignatureValid(PublicKey publicKey, byte[] signatureBytes, byte[] dataBytes)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(dataBytes);

        return signature.verify(signatureBytes);
    }

    /**
     * Deletes the files in the temp folder.
     */
    private void deleteFiles() {
        try {
            FileUtils.deleteDirectory(new File("data/temp/" + dirName));
        } catch (IOException e) {
            Logging.getLogger().warning("Could not delete files in folder: " + dirName);
        }
    }
}
