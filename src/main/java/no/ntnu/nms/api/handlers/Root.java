package no.ntnu.nms.api.handlers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = {"/"})
public class Root {

    @GetMapping(value={""})
    public String rootGetter() {
        return "This is the root handler";
    }

    @PostMapping(value={""})
    public String rootPost(@RequestBody String postString) {
        return "You posted: " + postString;
    }
}
