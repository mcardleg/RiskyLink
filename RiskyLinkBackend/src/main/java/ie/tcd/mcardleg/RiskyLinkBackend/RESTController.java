package ie.tcd.mcardleg.RiskyLinkBackend;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpResponse;

@RestController
public class RESTController {

    @GetMapping("/")
    public ResponseEntity runAlignmentAPI() {
        AlignmentGenerator.runGenerator();
        return ResponseEntity.ok("Alignments generated.");
    }

    @GetMapping("/create")
    public ResponseEntity create() {
        return ResponseEntity.ok("Repository created.");
    }

    @GetMapping("/delete")
    public ResponseEntity delete() {
        return ResponseEntity.ok("Repository deleted.");

    }

    @GetMapping("/uploadDataset")
    public ResponseEntity uploadDataset() {
        GraphDBHandler.uploadDataset("resources/councillor_salaries.ttl");
        return ResponseEntity.ok("Dataset uploaded.");
    }

    @GetMapping("/uploadOntology")
    public ResponseEntity uploadOntology() {
        return ResponseEntity.ok("Ontology uploaded.");
    }

}
