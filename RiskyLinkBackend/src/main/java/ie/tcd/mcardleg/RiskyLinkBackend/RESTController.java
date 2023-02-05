package ie.tcd.mcardleg.RiskyLinkBackend;

import org.eclipse.rdf4j.query.TupleQueryResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class RESTController {
    GraphDBHandler graphDBHandler = new GraphDBHandler();


    @GetMapping("/uploadDataset")
    public ResponseEntity uploadDataset() {
        graphDBHandler.addDataset("resources/councillor_salaries.ttl");
        return ResponseEntity.ok("Dataset uploaded.");
    }

    @GetMapping("/uploadOntology")
    public ResponseEntity uploadOntology() {
        graphDBHandler.addOntology("resources/councillor_salaries_ont.ttl");
        return ResponseEntity.ok("Ontology uploaded.");
    }

    @GetMapping("/generateAlignments")
    public ResponseEntity<List<List<QueryResult>>> runAlignmentAPI() {
        List<List<QueryResult>> results;

        String DBReadyResponse = graphDBHandler.checkDBReady();
        if (DBReadyResponse != Constants.READY){
            HttpHeaders headers = new HttpHeaders();
            headers.add("Error", DBReadyResponse);
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }

//        for (String alignmentPath : AlignmentGenerator.runGenerator()){
//            graphDBHandler.uploadTurtleFile(alignmentPath);
//        }

        results = graphDBHandler.runQueries();

        graphDBHandler.tearDownDB();

        //Order results

        return new ResponseEntity<>(results, HttpStatus.OK);
    }

}
