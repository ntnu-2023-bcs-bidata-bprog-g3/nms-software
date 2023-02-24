package no.ntnu.nms.licenseLedger;

import no.ntnu.nms.CustomerConstants;
import no.ntnu.nms.domainModel.Pool;
import no.ntnu.nms.domainModel.PoolRegistry;
import no.ntnu.nms.exception.LicenseGeneratorException;

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
            pool = PoolRegistry.getInstance()
                    .getPoolByMediaFunction(mediaFunction);
        } catch (NullPointerException e) {
            throw new LicenseGeneratorException("No pool with media function " + mediaFunction + " found");
        }
        if (!pool.subtractSeconds(duration)) throw new LicenseGeneratorException(
                "Not enough time left in pool");
        return pool;
    }

    private static String generateString(Pool pool, int duration) {
        HashMap<String, String> info = new HashMap<>();
        HashMap<String, String> license = new HashMap<>();
        HashMap<String, String> completeLicense = new HashMap<>();

        info.put("date", LocalTime.now().toString());
        info.put("customer", CustomerConstants.CUSTOMER_NAME);
        info.put("issuer", "NMS");
        info.put("uid", String.valueOf((long) (Math.random() * 1000000000000000000L)));

        license.put("name", pool.getMediaFunction());
        license.put("duration", String.valueOf(duration));
        license.put("description", pool.getDescription());


        completeLicense.put("info", info.toString());
        completeLicense.put("license", license.toString());

        System.out.println(completeLicense);
        return completeLicense.toString();
    }

    private static void writeToFile(String content) {
        // TODO: Implement method
    }
}
