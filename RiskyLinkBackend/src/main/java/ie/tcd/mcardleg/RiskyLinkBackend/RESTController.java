package ie.tcd.mcardleg.RiskyLinkBackend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RESTController {

    @GetMapping("/")
    public void runAlignmentAPI() {
        Skeleton.skel();
    }

}
