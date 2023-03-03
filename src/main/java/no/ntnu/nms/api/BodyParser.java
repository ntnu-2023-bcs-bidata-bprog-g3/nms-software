package no.ntnu.nms.api;

import no.ntnu.nms.lfa.LfaRegistry;
import no.ntnu.nms.logging.Logging;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BodyParser {

    public static Map<String, String> parseLfaBody(String payload) {
        JSONObject body;
        HashMap<String, String> returnMap = new HashMap<>();

        String ip;
        String mediaFunction;
        int duration;
        try {
            body = new JSONObject(payload);
            ip = body.getString("ip");
            mediaFunction = body.getString("mediaFunction");
            duration = body.getInt("duration");
        } catch (JSONException e) {
            Logging.getLogger().info("Failed to parse payload: " + e.getMessage());
            returnMap.put("Error", "{\"error\": \"Failed to parse payload\"}");
            return returnMap;
        }

        if (Objects.equals(ip, "") || Objects.equals(mediaFunction, "") || duration == 0) {
            Logging.getLogger().info("Missing parameter values");
            returnMap.put("Error", "{\"error\": \"Missing parameter values\"}");
            return returnMap;
        }

        LfaRegistry.getInstance().refreshLfaMap();
        if (!LfaRegistry.getInstance().lfaInRegistry(ip)) {
            Logging.getLogger().info("LFA not in registry");
            returnMap.put("Error", "{\"error\": \"LFA not in registry\"}");
            return returnMap;
        }

        returnMap.put("ip", ip);
        returnMap.put("mediaFunction", mediaFunction);
        returnMap.put("duration", String.valueOf(duration));
        return returnMap;
    }
}
