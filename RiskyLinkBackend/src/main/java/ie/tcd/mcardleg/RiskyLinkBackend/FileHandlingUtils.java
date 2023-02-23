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

    public static String generateTempDirectoryName(String sessionId, String filename) {
         return String.format("temp_files_%s/%s", sessionId, filename);
    }

    public static Path fileUpload(String sessionId, MultipartFile file) {
        Path path = null;
        try {
            byte[] bytes = file.getBytes();
            path = Paths.get(generateTempDirectoryName(sessionId, file.getOriginalFilename()));
            log.info(path.toString());
            Files.write(path, bytes);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return path;
    }

    public static void deleteSessionFiles(String sessionId) {
        try {
            String repoDirectory = System.getProperty("user.dir") + "/" + sessionId;
            log.info("Deleting: " + repoDirectory);
            File repo = new File(repoDirectory);
            FileUtils.deleteDirectory(repo);

            String tempDirectory = System.getProperty("user.dir") + "/" + generateTempDirectoryName(sessionId, "");
            log.info("Deleting: " + tempDirectory);
            File temp = new File(tempDirectory);
            FileUtils.deleteDirectory(temp);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

    }

}
