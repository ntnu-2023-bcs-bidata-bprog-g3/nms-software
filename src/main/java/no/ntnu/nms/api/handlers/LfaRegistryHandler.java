package no.ntnu.nms.api.handlers;

import no.ntnu.nms.exception.LfaRegistryException;
import no.ntnu.nms.lfa.LfaRegistry;
import no.ntnu.nms.logging.Logging;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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
    @GetMapping(value={"/"}, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public String getAllLfa() {
        Logging.getLogger().info("LFA endpoint called for all");
        LfaRegistry.getInstance().refreshLfaMap();
        return LfaRegistry.getInstance().jsonify();
    }

    /**
     * Register a new LFA.
     * @param name {@link String} name of the LFA.
     * @param request {@link HttpServletRequest} the request.
     * @return {@link String} JSON representation of the result.
     */
    @PostMapping(value={"/register"}, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public String registerLfa(@RequestParam String name, HttpServletRequest request) {
        Logging.getLogger().info("LFA endpoint called for register");
        try {
            LfaRegistry.getInstance().addLfa(request.getRemoteAddr()
                    + request.getRemotePort(), name);
            return "{\"success\": \"LFA registered with name " + name + "\"}";
        } catch (LfaRegistryException e) {
            Logging.getLogger().info("Failed to register LFA with name " + name + ": "
                    + e.getMessage());
            return "{\"error\": \"Failed to register LFA with name " + name + ": "
                    + e.getMessage() + "\"}";
        }

    }
}
