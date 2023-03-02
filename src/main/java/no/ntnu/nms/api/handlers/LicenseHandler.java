package no.ntnu.nms.api.handlers;

import no.ntnu.nms.api.client.Client;
import no.ntnu.nms.exception.LicenseGeneratorException;
import no.ntnu.nms.exception.ParserException;
import no.ntnu.nms.lfa.LfaRegistry;
import no.ntnu.nms.licenseLedger.LicenseGenerator;
import no.ntnu.nms.logging.Logging;
import no.ntnu.nms.parser.LicenseParser;
import org.apache.commons.io.FilenameUtils;
import org.apache.hc.core5.http.HttpException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.zip.ZipInputStream;

import static no.ntnu.nms.api.Constants.BASE_URL;

/**
 * LicenseHandler is a handler for the license API endpoint.
 * It is used to upload a license file and calls the parser.
 */
@CrossOrigin
@RestController
@RequestMapping(value = {BASE_URL + "/license"})
public class LicenseHandler {

    /**
     * Upload a license file.
     * @param file {@link MultipartFile} the license file.
     * @return {@link String} a message that the file was uploaded.
     */
    @PostMapping(value={""})
    public String licensePost(@RequestBody Optional<MultipartFile> file) throws IOException {
        Logging.getLogger().info("License endpoint called");
        if (file.isEmpty() || file.get().getSize() == 0) {
            Logging.getLogger().info("No file uploaded");
            return "{\"error\": \"No file uploaded\"}";
        }

        MultipartFile realFile = file.get();

        if (!Objects.equals(FilenameUtils.getExtension(realFile.getOriginalFilename()), "zip")) {
            Logging.getLogger().info(realFile.getName() + " is not a zip file");
            return "{\"error\": \"" + realFile.getName() + " is not a zip file\"}";
        }

        LicenseParser parser = new LicenseParser();
        try {
            parser.parse(new ZipInputStream(realFile.getInputStream()));
        } catch (ParserException e) {
            Logging.getLogger().info("Failed to parse license file: " + e.getMessage());
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
        return "{\"message\": \"File uploaded :)\"}";
    }

    /**
     * Generate a sub-license and upload it to the LFA.
     * @param payload {@link String} the payload containing the LFA IP, media function and duration.
     * @return {@link String} a message that the sub-license was generated.
     */
    @PostMapping(value={"/lfa"})
    public String generateSubLicense(@RequestBody String payload) {
        JSONObject body;

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
            return "{\"error\": \"Failed to parse payload\"}";
        }

        if (Objects.equals(ip, "") || Objects.equals(mediaFunction, "") || duration == 0) {
            Logging.getLogger().info("Missing parameter values");
            return "{\"error\": \"Missing parameter values\"}";
        }

        LfaRegistry.getInstance().refreshLfaMap();
        if (!LfaRegistry.getInstance().lfaInRegistry(ip)) {
            Logging.getLogger().info("LFA not in registry");
            return "{\"error\": \"LFA not in registry\"}";
        }

        String path;
        try {
            path = LicenseGenerator.generateLicense(ip, mediaFunction, duration);
        } catch (LicenseGeneratorException e) {
            Logging.getLogger().info("Failed to generate sub-license: " + e.getMessage());
            return "{\"error\": \"Failed to generate sub-license: " + e.getMessage() + "\"}";
        }

        try {
            Client.uploadLicense(ip, path);
        } catch (HttpException e) {
            Logging.getLogger().info("Failed to upload sub-license: " + e.getMessage());
            return "{\"error\": \"Failed to upload sub-license: \n" + e.getMessage() + "\"}";
        }

        return "{\"message\": \"Sub-license generated :)\"}";
    }
}
