package no.ntnu.nms.api.handlers;

import no.ntnu.nms.logging.Logging;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static no.ntnu.nms.api.Constants.BASE_URL;

import no.ntnu.nms.domainmodel.PoolRegistry;
import org.springframework.web.server.ResponseStatusException;

/**
 * PoolHandler is a handler for the pool API endpoint.
 */
@CrossOrigin
@RestController
@RequestMapping(value = {BASE_URL + "/pool"})
public class PoolHandler {

    /**
     * Get a pool by media function.
     * @param mediaFunction {@link String} media function of the pool.
     * @return {@link String} JSON representation of the pool.
     * @throws ResponseStatusException if no pool with the given media function is found.
     */
    @GetMapping(value = {"/{mediaFunction}"})
    @ResponseStatus(HttpStatus.OK)
    public String poolGetter(@PathVariable String mediaFunction) {
        Logging.getLogger().info("Pool endpoint called with media function " + mediaFunction);
        try {
            return PoolRegistry.getInstance(false)
                    .getPoolByMediaFunction(mediaFunction)
                    .jsonify();
        } catch (NullPointerException e) {
            Logging.getLogger().info("Failed to find pool with media function " + mediaFunction);
            return "{\"error\": \"No pool with media function " + mediaFunction + " found\"}";
        }

    }

    /**
     * Get all pools.
     * @return {@link String} JSON representation of all pools.
     */
    @GetMapping(value = {"/all"}, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public String getAllPools() {
        Logging.getLogger().info("Pool endpoint called for all");
        if (!PoolRegistry.getInstance(false).getPoolList().hasNext()) {
            Logging.getLogger().info("No pools found");
            return "{\"error\": \"No pools found\"}";
        }
        return PoolRegistry.getInstance(false).jsonify();
    }
}
