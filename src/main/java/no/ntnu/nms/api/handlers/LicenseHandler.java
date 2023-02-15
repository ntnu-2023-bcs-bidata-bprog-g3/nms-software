package no.ntnu.nms.api.handlers;

import no.ntnu.nms.logging.Logging;
import no.ntnu.nms.parser.LicenseParser;
import org.apache.commons.io.FilenameUtils;
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

        LicenseParser parser = new LicenseParser(new ZipInputStream(file.get().getInputStream()));
        parser.parse();

        return "Successfully uploaded " + realFile.getName();
    }
}
