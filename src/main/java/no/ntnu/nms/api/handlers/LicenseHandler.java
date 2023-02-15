package no.ntnu.nms.api.handlers;

import no.ntnu.nms.logging.Logging;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Optional;

import static no.ntnu.nms.api.Constants.BASE_URL;

/**
 * LicenseHandler is a handler for the license API endpoint.
 * It is used to upload a license file and calls the parser.
 */
@RestController
@RequestMapping(value = {BASE_URL + "/license"})
public class LicenseHandler {

    /**
     * Upload a license file.
     * @param file {@link MultipartFile} the license file.
     * @return {@link String} a message that the file was uploaded.
     */
    @PostMapping(value={""})
    public String licensePost(@RequestBody Optional<MultipartFile> file) {
        Logging.getLogger().info("License endpoint called");
        if (file.isEmpty() || file.get().getSize() == 0) {
            Logging.getLogger().info("No file uploaded");
            return "{\"error\": \"No file uploaded\"}";
        }

        MultipartFile realFile = file.get();

        if (!Objects.equals(FilenameUtils.getExtension(realFile.getOriginalFilename()), "zip")) {
            Logging.getLogger().info(realFile.getOriginalFilename() + " is not a zip file");
            return "{\"error\": \"" + realFile.getOriginalFilename() + " is not a zip file\"}";
        }

        // Implement the zip file parser here

        return "Successfully uploaded " + realFile.getOriginalFilename();
    }
}
