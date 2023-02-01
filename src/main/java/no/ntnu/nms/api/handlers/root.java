package no.ntnu.nms.api.handlers;

import org.springframework.web.bind.annotation.*;

import static no.ntnu.nms.api.Constants.BASE_URL;

@RestController
@RequestMapping(value = {BASE_URL})
public class root {

    @GetMapping(value={""})
    public String rootEndpoint() {
        return "This endpoint is not in use. Please check the documentation for available endpoints.";
    }

    @PostMapping(value={""})
    public String rootPost(@RequestBody String postString) {
        return "You posted: " + postString;
    }
}
