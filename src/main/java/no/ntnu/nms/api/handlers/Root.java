package no.ntnu.nms.api.handlers;

import no.ntnu.nms.logging.Logging;
import org.springframework.web.bind.annotation.*;

import static no.ntnu.nms.api.Constants.BASE_URL;

/**
 * root is a handler for the root API endpoint, which is not in use.
 */
@RestController
@RequestMapping(value = {BASE_URL})
public class Root {

    /**
     * Root endpoint handler method for a request of method GET.
     * @return {@link String} a message that the endpoint is not in use.
     */
    @GetMapping(value={""})
    public String rootEndpoint() {
        Logging.getLogger().info("Root endpoint called");
        return "This endpoint is not in use. Please check the documentation for available endpoints.";
    }
}
