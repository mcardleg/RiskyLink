package ie.tcd.mcardleg.RiskyLinkBackend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
public class RESTController {
    private DBHandler dbHandler = new DBHandler();
    private Logger log = LoggerFactory.getLogger(RESTController.class);


    @PostMapping("/uploadDataset")
    public ResponseEntity<String> uploadDataset(@RequestHeader("sessionID") String sessionId, @RequestPart("file") MultipartFile file) {
        if (null == file.getOriginalFilename()) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Error", "No file passed.");
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }
        dbHandler.addDataset(sessionId, FileHandlingUtils.fileUpload(sessionId, file));

        return ResponseEntity.ok("Dataset uploaded.");
    }

    @PostMapping("/uploadOntology")
    public ResponseEntity<String> uploadOntology(@RequestHeader("sessionID") String sessionId, @RequestPart("file") MultipartFile file) {
        if (null == file.getOriginalFilename()) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Error", "No file passed.");
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }
        dbHandler.addOntology(sessionId, FileHandlingUtils.fileUpload(sessionId, file));

        return ResponseEntity.ok("Ontology uploaded.");
    }

    @GetMapping("/runQueries")
    public ResponseEntity<Map<String, List<QueryResult>>> runQueries(@RequestHeader("sessionID") String sessionId) {
        return new ResponseEntity<>(dbHandler.runQueries(sessionId), HttpStatus.OK);
    }

    @GetMapping("/sessionEnded")
    public ResponseEntity<String> sessionEnded(@RequestHeader("sessionID") String sessionId) {
        log.info(sessionId);
        dbHandler.tearDownDB(sessionId);
//        FileHandlingUtils.deleteSessionFiles(sessionId);
        //Delete files
        return ResponseEntity.ok("Session shutdown");
    }



}
