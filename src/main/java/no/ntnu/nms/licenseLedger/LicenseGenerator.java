package no.ntnu.nms.licenseLedger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ntnu.nms.domainModel.Pool;
import no.ntnu.nms.domainModel.PoolRegistry;
import no.ntnu.nms.exception.FileHandlerException;
import no.ntnu.nms.exception.LicenseGeneratorException;
import no.ntnu.nms.file_handler.FileHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class LicenseGenerator {

    private static final String TEMP_FILE_PATH = "data/sublicense/";


    public static String generateLicense(String ip, String mediafunction, int duration) throws LicenseGeneratorException {
        if (ip == null ||ip.length() == 0 || mediafunction == null
                || mediafunction.length() == 0 || duration < 1) {
            throw new LicenseGeneratorException("Invalid input");
        }
        Pool pool = getPoolAndSubtract(mediafunction, duration);

        int uid = (int) (Math.random() * 1000000000);
        String path = TEMP_FILE_PATH + uid + "/";

        try {
            writeToFile(path, generateString(pool, duration));
            signFile(path + "license.json");
        } catch (LicenseGeneratorException e) {
            throw new LicenseGeneratorException(e.getMessage());
        }

        return path;
    }

    private static void signFile(String path) throws LicenseGeneratorException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", "openssl dgst -sha256 -sign intermediate-pk.key -out " + path + ".signature "  + path);
        try {
            Process process = processBuilder.inheritIO().start();
            int returnCode = process.waitFor();
            if (returnCode != 0) {
                throw new LicenseGeneratorException("Failed to sign file");
            }
        } catch (Exception e) {
            throw new LicenseGeneratorException("Failed to sign file: " + e.getMessage());
        }
    }

    private static void writeToFile(String path, String content) throws LicenseGeneratorException {
        //write to file
        try {
            FileHandler.writeStringToFile(content, path + "license.json");
        } catch (FileHandlerException e) {
            throw new LicenseGeneratorException("Failed to write to file: " + e.getMessage());
        }
    }

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

    private static String generateString(Pool pool, int duration) {
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

        licenseMap.put("info", infoMap);
        licenseMap.put("license", Collections.singletonMap("keys", keysList));

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(licenseMap);
        } catch (JsonProcessingException e) {
            throw new LicenseGeneratorException("Failed to generate string: " + e.getMessage());
        }
    }
}
