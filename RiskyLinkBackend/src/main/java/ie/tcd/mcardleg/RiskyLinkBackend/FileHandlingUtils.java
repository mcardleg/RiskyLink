package ie.tcd.mcardleg.RiskyLinkBackend;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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

    public static void writeTickedRowsToFile(String sessionId, List<ClassPair> tickedRows) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("tickedRows/" + sessionId + ".txt"));
            for (ClassPair classPair: tickedRows) {
                writer.print(classPair);
            }
            writer.close();
            log.info("Wrote ticked rows to file.");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

    }

    public static void deleteSessionFiles(String sessionId) {
        try {
            String repoDirectory = System.getProperty("user.dir") + "/" + sessionId;
            File repo = new File(repoDirectory);
            if (repo.exists()) {
                log.info("Deleting: " + repoDirectory);
                FileUtils.deleteDirectory(repo);
            }

            String tempDirectory = System.getProperty("user.dir") + "/" + generateTempDirectoryName(sessionId, "");
            File temp = new File(tempDirectory);
            if (temp.exists()) {
                log.info("Deleting: " + tempDirectory);
                FileUtils.deleteDirectory(temp);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

    }

}
