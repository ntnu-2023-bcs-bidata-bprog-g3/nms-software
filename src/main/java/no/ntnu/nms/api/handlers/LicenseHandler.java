package no.ntnu.nms.api.handlers;

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

        if (file.isEmpty() || file.get().getSize() == 0) {
            return "{\"error\": \"No file uploaded\"}";
        }

        MultipartFile realFile = file.get();

        if (!Objects.equals(FilenameUtils.getExtension(realFile.getOriginalFilename()), "zip")) {
            return "{\"error\": \"" + realFile.getOriginalFilename() + " is not a zip file\"}";
        }

        // Implement the zip file parser here

        return "Successfully uploaded " + realFile.getOriginalFilename();
    }
}
