package ie.tcd.mcardleg.RiskyLinkBackend;

import org.eclipse.rdf4j.query.TupleQueryResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class RESTController {
    GraphDBHandler graphDBHandler = new GraphDBHandler();
    String filePath;

    @PostMapping("/uploadDataset")
    public ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file) {
        if (null == file.getOriginalFilename()) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Error", "No file passed.");
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(file.getOriginalFilename());
            filePath = path.toString();

            Files.write(path, bytes);
            graphDBHandler.addDataset(path.toString());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.ok("Dataset uploaded.");
    }

    @PostMapping("/uploadOntology")
    public ResponseEntity<String> uploadOntology(@RequestPart("file") MultipartFile file) {
        if (null == file.getOriginalFilename()) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Error", "No file passed.");
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(file.getOriginalFilename());
            Files.write(path, bytes);
            graphDBHandler.addOntology(path.toString());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.ok("Ontology uploaded.");
    }

    @GetMapping("/printFiles")
    public void printFiles() throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(filePath));
        String line = in.readLine();
        while(line != null)
        {
            System.out.println(line);
            line = in.readLine();
        }
        in.close();

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

        for (String alignmentPath : AlignmentGenerator.runGenerator()){
            graphDBHandler.uploadTurtleFile(alignmentPath);
        }

        results = graphDBHandler.runQueries();

        graphDBHandler.tearDownDB();

        //Order results

        return new ResponseEntity<>(results, HttpStatus.OK);
    }

}
