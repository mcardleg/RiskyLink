package ie.tcd.mcardleg.RiskyLinkBackend;

import fr.inrialpes.exmo.align.impl.method.*;
import org.semanticweb.owl.align.AlignmentProcess;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class RESTController {

    @GetMapping("/")
    public void runAlignmentAPI() {
        HashMap<String, AlignmentProcess> aligners = new HashMap<String, AlignmentProcess>();
        aligners.put("ClassStruct", new ClassStructAlignment());
        aligners.put("EditDistName", new EditDistNameAlignment());
        aligners.put("NameAndProperty", new NameAndPropertyAlignment());
        aligners.put("NameEq", new NameEqAlignment());
        aligners.put("SMOAName", new SMOANameAlignment());
        aligners.put("StringDist", new StringDistAlignment());
        aligners.put("StrucSubsDist", new StrucSubsDistAlignment());
        aligners.put("SubsDistName", new SubsDistNameAlignment());

        for (Map.Entry<String, AlignmentProcess> set : aligners.entrySet()) {
            AlignmentGenerator.generate("resources/example.owl", set.getKey(), set.getValue());
        }
    }

}
