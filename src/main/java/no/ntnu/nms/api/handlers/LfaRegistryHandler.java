package no.ntnu.nms.api.handlers;

import no.ntnu.nms.api.BodyParser;
import no.ntnu.nms.api.client.Client;
import no.ntnu.nms.exception.ExceptionHandler;
import no.ntnu.nms.exception.LfaRegistryException;
import no.ntnu.nms.lfa.LfaRegistry;
import no.ntnu.nms.logging.Logging;
import org.apache.hc.core5.http.HttpException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.Map;

import static no.ntnu.nms.api.Constants.BASE_URL;

/**
 * LfaRegistryHandler is a handler for the lfa API endpoint.
 */
@CrossOrigin
@RestController
@RequestMapping(value = {BASE_URL + "/lfa"})
public class LfaRegistryHandler {

    /**
     * Get all LFA.
     * @return {@link String} JSON representation of all LFA.
     */
    @GetMapping(value={""}, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public String getAllLfa() {
        Logging.getLogger().info("LFA endpoint called for all");
        LfaRegistry.getInstance().refreshLfaMap();
        return LfaRegistry.getInstance().jsonify();
    }

    /**
     * Get all LFA with their licenses.
     * @return {@link String} JSON representation of all LFA with their licenses.
     */
    @GetMapping(value={"/licenses"}, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public String getLicenses() {
        Logging.getLogger().info("LFA endpoint called for all licenses");
        LfaRegistry.getInstance().refreshLfaMap();
        if (LfaRegistry.getInstance().getSize() == 0) {
            Logging.getLogger().info("No LFA found");
            return "{\"error\": \"No LFA found\"}";
        }
        JSONObject lfasBeforeLicense = new JSONObject(LfaRegistry.getInstance().jsonify());
        JSONObject lfasWithLicense = new JSONObject("{\"lfas\": []}");
        for (Object currentLfa : lfasBeforeLicense.getJSONArray("lfas")) {
            JSONObject lfaJson = (JSONObject) currentLfa;
            JSONArray lfaLicenses = Client.getLfaLicenses(lfaJson.getString("ip"));
            if (lfaLicenses != null) {
                lfaJson.put("licenses", lfaLicenses);
            }
            lfasWithLicense.getJSONArray("lfas").put(lfaJson);
        }
        return lfasWithLicense.toString();
    }

    /**
     * Register a new LFA.
     * @param name {@link String} name of the LFA.
     * @param request {@link HttpServletRequest} the request.
     * @return {@link String} JSON representation of the result.
     */
    @PutMapping(value={"/register"}, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public String registerLfa(@RequestParam String name, @RequestParam String port,
                              HttpServletRequest request) {
        Logging.getLogger().info("LFA endpoint called for register");
        try {
            LfaRegistry.getInstance().addLfa(request.getRemoteAddr() + ":" + port, name);
            return "{\"success\": \"LFA registered with name " + name + "\"}";
        } catch (LfaRegistryException e) {
            Logging.getLogger().info("Failed to register LFA with name " + name + ": "
                    + e.getMessage());
            return "{\"error\": \"Failed to register LFA with name " + name + ": "
                    + ExceptionHandler.getMessage(e) + "\"}";
        }
    }

    /**
     * Consume parts of a license.
     * @param payload {@link String} the payload containing the LFA IP, media function and duration.
     * @return {@link String} a message that the license was consumed.
     */
    @PutMapping(value={"/consume"})
    public String consumeLicense(@RequestBody String payload) {
        Map<String, String> body = BodyParser.parseLfaBody(payload);

        if (body.containsKey("error")) {
            return body.get("error");
        }

        try {
            Client.consumeLicense(new JSONObject(body));
        } catch (HttpException | JSONException e) {
            Logging.getLogger().info("Failed to consume license: " + e.getMessage());
            return "{\"error\": \"Failed to consume license: " + ExceptionHandler.getMessage(e) + "\"}";
        }
        return "{\"message\": \"License consumed :)\"}";
    }
}
