package no.ntnu.nms.license;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ntnu.nms.App;
import no.ntnu.nms.domainModel.Pool;
import no.ntnu.nms.domainModel.PoolRegistry;
import no.ntnu.nms.exception.FileHandlerException;
import no.ntnu.nms.exception.LicenseGeneratorException;
import no.ntnu.nms.file_handler.FileHandler;
import no.ntnu.nms.lfa.LfaRegistry;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Class for generating licenses.
 */
public class LicenseGenerator {

    /**
     * Path to temporary files.
     */
    private static final String TEMP_FILE_PATH = "data/sublicense/";


    /**
     * Generates a license.
     * @param ip IP address of the client
     * @param mediafunction Media function to generate license for
     * @param duration Duration of the license
     * @return Path to the license file
     * @throws LicenseGeneratorException If the license could not be generated
     */
    public static String generateLicense(String ip, String mediafunction, int duration) throws LicenseGeneratorException {
        if (ip == null ||ip.length() == 0 || mediafunction == null
                || mediafunction.length() == 0 || duration < 1) {
            throw new LicenseGeneratorException("Invalid input");
        }
        Pool pool = getPoolAndSubtract(mediafunction, duration);

        int uid = (int) (Math.random() * 1000000000);
        String path = TEMP_FILE_PATH + uid + "/";

        String name = LfaRegistry.getInstance().getLfaName(ip);

        try {
            writeToFile(path, generateString(pool, duration, name));
            PrivateKey privateKey = getPrivateKey();
            signFile(path + "license.json", privateKey);
        } catch (LicenseGeneratorException e) {
            throw new LicenseGeneratorException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return path;
    }

    /**
     * Signs a file.
     * @param path Path to the file to sign
     * @param privateKey Private key to use for signing
     * @throws LicenseGeneratorException If the file could not be signed
     */
    private static void signFile(String path, PrivateKey privateKey) throws LicenseGeneratorException {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);

            byte[] fileBytes = Files.readAllBytes(Paths.get(path));
            signature.update(fileBytes);

            byte[] signedBytes = signature.sign();

            Path signaturePath = Paths.get(path + ".signature");
            Files.write(signaturePath, signedBytes);
        } catch (Exception e) {
            throw new LicenseGeneratorException("Failed to sign file: " + e.getMessage());
        }
    }

    /**
     * Writes the license string to a file.
     * @param path Path to the license file
     * @param content Content to write to the file
     * @throws LicenseGeneratorException If the license could not be generated
     */
    private static void writeToFile(String path, String content) throws LicenseGeneratorException {
        //write to file
        try {
            FileHandler.writeStringToFile(content, path + "license.json");
        } catch (FileHandlerException e) {
            throw new LicenseGeneratorException("Failed to write to file: " + e.getMessage());
        }
    }

    /**
     * Gets the pool and subtracts the duration from it.
     * @param mediaFunction Mediafunction to subtract from
     * @param duration Duration to subtract
     * @return The pool
     * @throws LicenseGeneratorException If the subtraction fails
     */
    private static Pool getPoolAndSubtract(String mediaFunction, int duration) throws LicenseGeneratorException {
        Pool pool;
        try {
            pool = PoolRegistry.getInstance(false)
                    .getPoolByMediaFunction(mediaFunction);
            if (pool == null) {
                throw new NullPointerException();
            }
        } catch (NullPointerException e) {
            throw new LicenseGeneratorException("No pool with media function " + mediaFunction + " found");
        }
        if (!pool.subtractSeconds(duration)) throw new LicenseGeneratorException(
                "Not enough time left in pool");
        return pool;
    }

    /**
     * Generates a license string.
     * @param pool Pool to generate license for
     * @param duration Duration of the license
     * @return The license string
     */
    private static String generateString(Pool pool, int duration, String name) {
        HashMap<String, Object> licenseMap = new HashMap<>();
        HashMap<String, Object> infoMap = new HashMap<>();
        ArrayList<HashMap<String, Object>> keysList = new ArrayList<>();
        HashMap<String, Object> keyMap = new HashMap<>();

        infoMap.put("date", LocalDateTime.now().toString());
        infoMap.put("customer", "TV2");
        infoMap.put("issuer", "NMS");
        //infoMap.put("uid", pool.getId().toString());

        keyMap.put("name", pool.getMediaFunction());
        keyMap.put("duration", duration);
        keyMap.put("description", pool.getDescription());

        keysList.add(keyMap);
        licenseMap.put("name", name);
        licenseMap.put("info", infoMap);
        licenseMap.put("license", Collections.singletonMap("keys", keysList));

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(licenseMap);
        } catch (JsonProcessingException e) {
            throw new LicenseGeneratorException("Failed to generate string: " + e.getMessage());
        }
    }

    /**
     * Gets the private key from the keystore.
     * @return The private key
     * @throws LicenseGeneratorException If the private key could not be retrieved
     */
    private static PrivateKey getPrivateKey() throws LicenseGeneratorException {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            InputStream is = App.class.getClassLoader().getResourceAsStream("keystore.jks");
            /*
            For demonstration purposes, the keystore password is "secret" and it is written in-line.
            For production environments, DO NOT add secrets to version control systems.
            A better solution would be to derive a password from a mathematical function of
            some customer unique information. See @link{KeyGenerator} as an example.
             */
            keyStore.load(is, "secret".toCharArray());

            return (PrivateKey) keyStore.getKey("keystore", "secret".toCharArray());
        } catch (Exception e) {
            throw new LicenseGeneratorException("Failed to get private key: " + e.getMessage());
        }
    }

}
