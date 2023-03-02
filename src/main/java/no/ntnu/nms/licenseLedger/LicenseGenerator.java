package no.ntnu.nms.licenseLedger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ntnu.nms.domainModel.Pool;
import no.ntnu.nms.domainModel.PoolRegistry;
import no.ntnu.nms.exception.LicenseGeneratorException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import java.time.LocalTime;

public class LicenseGenerator {

    /*
    {
    "info": {
        "date": "2021-08-03 07:49:51",
        "customer": "TV2",
        "issuer": "Nevion",
        "uid": "fur21o1-12590521mf-155125"
    },
    "license": {
        "name": "J2KHDX",
        "duration": 100,
        "description": "J2K HD Encoders/Decoders"
    }
}
     */

    private static final String TEMP_FILE_PATH = "data/license/temp.json";


    public static void generateLicense(String ip, String mediafunction, int duration) {
        if (ip == null ||ip.length() == 0 || mediafunction == null
                || mediafunction.length() == 0 || duration < 1) {
            throw new LicenseGeneratorException("Failed to generate license: Invalid input");
        }
        Pool pool;
        try {
            pool = getPoolAndSubtract(mediafunction, duration);
        } catch (LicenseGeneratorException e) {
            throw new LicenseGeneratorException("Failed to generate license:" + e.getMessage());
        }

        String content = generateString(pool, duration);

    }

    private static Pool getPoolAndSubtract(String mediaFunction, int duration) throws LicenseGeneratorException {
        Pool pool;
        try {
            pool = PoolRegistry.getInstance(false)
                    .getPoolByMediaFunction(mediaFunction);
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
        infoMap.put("uid", pool.getId());

        keyMap.put("name", pool.getMediaFunction());
        keyMap.put("duration", duration);
        keyMap.put("description", pool.getDescription());

        keysList.add(keyMap);

        licenseMap.put("info", infoMap);
        licenseMap.put("license", Collections.singletonMap("keys", keysList));

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println(objectMapper.writeValueAsString(licenseMap));
            return objectMapper.writeValueAsString(licenseMap);
        } catch (JsonProcessingException e) {
            throw new LicenseGeneratorException("Failed to generate string: " + e.getMessage());
        }
    }

    private static void writeToFile(String content) {
        // TODO: Implement method
    }
}
