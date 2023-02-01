package no.ntnu.nms.api.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static no.ntnu.nms.api.Constants.BASE_URL;

import no.ntnu.nms.domain_model.PoolRegistry;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = {BASE_URL + "/pool"})
public class PoolHandler {
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

    @GetMapping(value={"/all"})
    @ResponseStatus(HttpStatus.OK)
    public String getAllPools() {
        if (!PoolRegistry.getInstance().getPoolList().hasNext()) {
            return "{\"error\": \"No pools found\"}";
        }
        return PoolRegistry.getInstance().jsonify();
    }

    @PostMapping(value={""})
    @ResponseStatus(HttpStatus.CREATED)
    public String rootPost(@RequestBody String postString) {
        return "You posted: " + postString;
    }
}
