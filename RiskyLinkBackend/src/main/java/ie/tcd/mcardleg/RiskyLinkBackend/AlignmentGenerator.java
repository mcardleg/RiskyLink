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

import static ie.tcd.mcardleg.RiskyLinkBackend.DBHandler.ETHICS_ONTOLOGOY_DIRECTORY;


public class AlignmentGenerator {

    private static final String CLASS_STRUCT_MATCHER = "ClassStruct";
    private static final String EDIT_DIST_NAME_MATCHER = "EditDistName";
    private static final String NAME_AND_PROPERTY_MATCHER = "NameAndProperty";
    private static final String NAME_EQ_MATCHER = "NameEq";
    private static final String SMOAN_NAME_MATCHER = "SMOAName";
    private static final String STRING_DIST_MATCHER = "StringDist";
    private static final String STRUC_SUBS_DIST_MATCHER = "StrucSubsDist";
    private static final String SUBS_DIST_NAME_MATCHER = "SubsDistName";

    private static Logger log = LoggerFactory.getLogger(AlignmentGenerator.class);

    public static List<String> runGenerator(String sessionId, String ontologyDirectory) {
        HashMap<String, AlignmentProcess> aligners = new HashMap<String, AlignmentProcess>();
        aligners.put(CLASS_STRUCT_MATCHER, new ClassStructAlignment());
        aligners.put(EDIT_DIST_NAME_MATCHER, new EditDistNameAlignment());
//        aligners.put(NAME_AND_PROPERTY_MATCHER, new NameAndPropertyAlignment());
//        aligners.put(NAME_EQ_MATCHER, new NameEqAlignment());
//        aligners.put(SMOAN_NAME_MATCHER, new SMOANameAlignment());
//        aligners.put(STRING_DIST_MATCHER, new StringDistAlignment());
//        aligners.put(STRUC_SUBS_DIST_MATCHER, new StrucSubsDistAlignment());
//        aligners.put(SUBS_DIST_NAME_MATCHER, new SubsDistNameAlignment());

        List<String> filePaths = new ArrayList<String>();
        for (Map.Entry<String, AlignmentProcess> set : aligners.entrySet()) {
            filePaths.add(generate(sessionId, ontologyDirectory, set.getKey(), set.getValue()));
        }
        log.info("Alignments generated.");
        return filePaths;
    }

    public static String generate(String sessionId, String ontologyDirectory, String alignerName, AlignmentProcess aligner) {
        String currentDirectory = System.getProperty("user.dir");
        String filePath = FileHandlingUtils.generateTempDirectoryName(
                sessionId, FilenameUtils.getBaseName(ontologyDirectory) + "_" + alignerName + ".owl");

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
        return filePath;
    }
}
