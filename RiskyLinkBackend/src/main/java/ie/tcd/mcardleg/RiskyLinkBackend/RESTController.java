package ie.tcd.mcardleg.RiskyLinkBackend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
public class RESTController {
    private DBHandler dbHandler = new DBHandler();
    private Logger log = LoggerFactory.getLogger(RESTController.class);

    @GetMapping("/startSession")
    public ResponseEntity<String> startSession(@RequestHeader("sessionID") String sessionId) {
        dbHandler.ensureSessionSetUp(sessionId);
        return ResponseEntity.ok("Session set up complete.");
    }

    @PostMapping("/uploadDataset")
    public ResponseEntity<String> uploadDataset(@RequestHeader("sessionID") String sessionId, @RequestPart("file") MultipartFile file) {
        if (null == file.getOriginalFilename()) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Error", "No file passed.");
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }
        if (dbHandler.addDataset(sessionId, FileHandlingUtils.fileUpload(sessionId, file))) {
            return ResponseEntity.ok("Dataset uploaded.");

        }

        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/uploadOntology")
    public ResponseEntity<String> uploadOntology(@RequestHeader("sessionID") String sessionId, @RequestPart("file") MultipartFile file) {
        if (null == file.getOriginalFilename()) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Error", "No file passed.");
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }
        if (dbHandler.addOntology(sessionId, FileHandlingUtils.fileUpload(sessionId, file))) {
            return ResponseEntity.ok("Ontology uploaded.");
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/runQueries")
    public ResponseEntity<HashMap<String, String>> runQueries(@RequestHeader("sessionID") String sessionId) {
        return new ResponseEntity<>(dbHandler.runQueries(sessionId), HttpStatus.OK);
    }

    @GetMapping("/getLinks")
    public ResponseEntity<List<Triple>> runQueries(
            @RequestParam("sessionID") String sessionId,
            @RequestParam("demographic") String demographic,
            @RequestParam("sensitiveInfo") String sensitiveInfo) {
        return new ResponseEntity<>(dbHandler.getLinks(sessionId, demographic, sensitiveInfo), HttpStatus.OK);
    }

    @GetMapping("/sessionEnded")
    public ResponseEntity<String> sessionEnded(@RequestHeader("sessionID") String sessionId) {
        dbHandler.tearDownDB(sessionId);
        FileHandlingUtils.deleteSessionFiles(sessionId);

        return ResponseEntity.ok("Session shutdown");
    }



}
