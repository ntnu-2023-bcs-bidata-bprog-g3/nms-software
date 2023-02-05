package no.ntnu.nms.api.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static no.ntnu.nms.api.Constants.BASE_URL;

import no.ntnu.nms.domain_model.PoolRegistry;
import org.springframework.web.server.ResponseStatusException;

/**
 * PoolHandler is a handler for the pool API endpoint.
 */
@RestController
@RequestMapping(value = {BASE_URL + "/pool"})
public class PoolHandler {

    /**
     * Get a pool by media function.
     * @param mediaFunction {@link String} media function of the pool.
     * @return {@link String} JSON representation of the pool.
     * @throws ResponseStatusException if no pool with the given media function is found.
     */
    @GetMapping(value={"/{mediaFunction}"})
    @ResponseStatus(HttpStatus.OK)
    public String poolGetter(@PathVariable String mediaFunction) {
        try {
            return PoolRegistry.getInstance()
                    .getPoolByMediaFunction(mediaFunction)
                    .jsonify();
        } catch (NullPointerException e) {
            return "{\"error\": \"No pool with media function " + mediaFunction + " found\"}";
        }

    }

    /**
     * Get all pools.
     * @return {@link String} JSON representation of all pools.
     */
    @GetMapping(value={"/all"})
    @ResponseStatus(HttpStatus.OK)
    public String getAllPools() {
        if (!PoolRegistry.getInstance().getPoolList().hasNext()) {
            return "{\"error\": \"No pools found\"}";
        }
        return PoolRegistry.getInstance().jsonify();
    }

    /**
     * A test handler for the method POST.
     * @param postString {@link String} the string to be posted.
     * @return {@link String} the string that was posted.
     */
    @PostMapping(value={""})
    @ResponseStatus(HttpStatus.CREATED)
    public String rootPost(@RequestBody String postString) {
        return "You posted: " + postString;
    }
}