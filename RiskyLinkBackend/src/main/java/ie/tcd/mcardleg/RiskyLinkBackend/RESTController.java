package ie.tcd.mcardleg.RiskyLinkBackend;

import fr.inrialpes.exmo.align.impl.method.*;
import org.semanticweb.owl.align.AlignmentProcess;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RESTController {

    @GetMapping("/")
    public void runAlignmentAPI() {
        List<AlignmentProcess> aligners = new ArrayList<AlignmentProcess>();
        aligners.add(new ClassStructAlignment());
        aligners.add(new EditDistNameAlignment());
        aligners.add(new NameAndPropertyAlignment());
        aligners.add(new NameEqAlignment());
        aligners.add(new SMOANameAlignment());
        aligners.add(new StringDistAlignment());
        aligners.add(new StrucSubsDistAlignment());
        aligners.add(new SubsDistNameAlignment());


        for (AlignmentProcess aligner : aligners) {
            AlignmentGenerator.generate("resources/example.owl", aligner);
        }
    }

}
