package ie.tcd.mcardleg.RiskyLinkBackend;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
public class RESTController {
    // GraphDBHandler graphDBHandler = new GraphDBHandler();
    DBHandler dbHandler = new DBHandler();
    String filePath;

    @PostMapping("/uploadDataset")
    public ResponseEntity<String> uploadDataset(@RequestHeader("sessionID") String sessionId, @RequestPart("file") MultipartFile file) {
        if (null == file.getOriginalFilename()) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Error", "No file passed.");
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(file.getOriginalFilename());

            Files.write(path, bytes);
            // graphDBHandler.addDataset(sessionId, path);
            dbHandler.addDataset(sessionId, path);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.ok("Dataset uploaded.");
    }

    @PostMapping("/uploadOntology")
    public ResponseEntity<String> uploadOntology(@RequestHeader("sessionID") String sessionId, @RequestPart("file") MultipartFile file) {
        if (null == file.getOriginalFilename()) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Error", "No file passed.");
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(file.getOriginalFilename());
            Files.write(path, bytes);
            // graphDBHandler.addOntology(sessionId, path);
            dbHandler.addOntology(sessionId, path);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.ok("Ontology uploaded.");
    }

    @GetMapping("/runQueries")
    public ResponseEntity<Map<String, List<QueryResult>>> runQueries(@RequestHeader("sessionID") String sessionId) {
        Map<String, List<QueryResult>> results = null;

        results = dbHandler.runQueries(sessionId);

        //Order results
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @GetMapping("/sessionEnded")
    public ResponseEntity<String> sessionEnded(@RequestHeader("sessionID") String sessionId) {
        // System.out.println(sessionId);
        // graphDBHandler.tearDownDB();
        dbHandler.tearDownDB(sessionId);
        return ResponseEntity.ok("Session shutdown");
    }



}
