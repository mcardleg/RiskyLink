package ie.tcd.mcardleg.RiskyLinkBackend;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHandlingUtils {

    private static Logger log = LoggerFactory.getLogger(FileHandlingUtils.class);

    public static Path fileUpload(String sessionId, MultipartFile file) {
        Path path = null;
        try {
            byte[] bytes = file.getBytes();
            path = Paths.get("temp_files_" + sessionId + "/" + file.getOriginalFilename());
            log.info(path.toString());
            Files.write(path, bytes);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return path;
    }

    public static void deleteSessionFiles(String sessionId) {
        //Delete files uploaded above
        //Delete alignments
        String currentDirectory = System.getProperty("user.dir") + "/" + sessionId;
        log.info(currentDirectory);
        File temp2 = new File(currentDirectory);
        temp2.delete();
        log.info("Deleted session resources.");
    }

}
