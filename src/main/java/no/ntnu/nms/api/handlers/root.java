package no.ntnu.nms.api.handlers;

import org.springframework.web.bind.annotation.*;
import static no.ntnu.nms.api.Constants.VERSION;

@RestController
@RequestMapping(value = {"/api/" + VERSION + "/"})
public class root {

    @GetMapping(value={""})
    public String rootGetter() {
        return "This is the root handler";
    }

    @PostMapping(value={""})
    public String rootPost(@RequestBody String postString) {
        return "You posted: " + postString;
    }
}
