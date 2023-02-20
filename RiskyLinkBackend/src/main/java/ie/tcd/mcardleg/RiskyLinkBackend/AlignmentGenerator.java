package ie.tcd.mcardleg.RiskyLinkBackend;

import fr.inrialpes.exmo.align.impl.method.*;
import fr.inrialpes.exmo.align.impl.renderer.OWLAxiomsRendererVisitor;
import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.AlignmentProcess;
import org.semanticweb.owl.align.AlignmentVisitor;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ie.tcd.mcardleg.RiskyLinkBackend.Constants.ETHICS_ONTOLOGOY_DIRECTORY;


public class AlignmentGenerator {
    private static Logger log = LoggerFactory.getLogger(AlignmentGenerator.class);

    public static List<String> runGenerator(String ontologyDirectory) {
        HashMap<String, AlignmentProcess> aligners = new HashMap<String, AlignmentProcess>();
        aligners.put("ClassStruct", new ClassStructAlignment());
        aligners.put("EditDistName", new EditDistNameAlignment());
        aligners.put("NameAndProperty", new NameAndPropertyAlignment());
        aligners.put("NameEq", new NameEqAlignment());
        aligners.put("SMOAName", new SMOANameAlignment());
        aligners.put("StringDist", new StringDistAlignment());
        aligners.put("StrucSubsDist", new StrucSubsDistAlignment());
        aligners.put("SubsDistName", new SubsDistNameAlignment());

        List<String> filePaths = new ArrayList<String>();
        for (Map.Entry<String, AlignmentProcess> set : aligners.entrySet()) {
            filePaths.add(generate(ontologyDirectory, set.getKey(), set.getValue()));
        }
        log.info("Alignments generated.");
        return filePaths;
    }

    public static String generate(String ontologyDirectory, String alignerName, AlignmentProcess aligner) {
        String currentDirectory = System.getProperty("user.dir");
        String filePath = String.format("resources/%s_%s.owl",
                FilenameUtils.getBaseName(ontologyDirectory), alignerName);

        URI onto1 = null;
        URI onto2 = null;
        Properties params = new Properties();

        try {
            onto1 = new URI(String.format("file://%s/%s", currentDirectory, ETHICS_ONTOLOGOY_DIRECTORY));
            onto2 = new URI(String.format("file://%s/%s", currentDirectory, ontologyDirectory));

            aligner.init ( onto1, onto2 );
            aligner.align( (Alignment)null, params );

            FileOutputStream file = new FileOutputStream(filePath);

            PrintWriter writer = new PrintWriter(file, true);

            AlignmentVisitor renderer = new OWLAxiomsRendererVisitor(writer);
            aligner.render(renderer);
            writer.flush();
            writer.close();

        } catch (IOException | URISyntaxException | AlignmentException e) {
            log.error(e.getMessage(), e);
        }
        log.info("Alignment generated: " + filePath);
        return filePath;
    }
}
