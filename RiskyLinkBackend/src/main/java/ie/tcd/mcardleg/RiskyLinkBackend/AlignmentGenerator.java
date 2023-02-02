package ie.tcd.mcardleg.RiskyLinkBackend;

import fr.inrialpes.exmo.align.impl.method.*;
import fr.inrialpes.exmo.align.impl.renderer.OWLAxiomsRendererVisitor;
import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentProcess;
import org.semanticweb.owl.align.AlignmentVisitor;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.ResponseEntity;

import static ie.tcd.mcardleg.RiskyLinkBackend.Constants.ETHICS_ONTOLOGOY_DIRECTORY;


public class AlignmentGenerator {
    public static void runGenerator() {
        HashMap<String, AlignmentProcess> aligners = new HashMap<String, AlignmentProcess>();
        aligners.put("ClassStruct", new ClassStructAlignment());
        aligners.put("EditDistName", new EditDistNameAlignment());
        aligners.put("NameAndProperty", new NameAndPropertyAlignment());
        aligners.put("NameEq", new NameEqAlignment());
        aligners.put("SMOAName", new SMOANameAlignment());
        aligners.put("StringDist", new StringDistAlignment());
        aligners.put("StrucSubsDist", new StrucSubsDistAlignment());
        aligners.put("SubsDistName", new SubsDistNameAlignment());

        for (Map.Entry<String, AlignmentProcess> set : aligners.entrySet()) {
            generate("resources/example.owl", set.getKey(), set.getValue());
        }
    }

    public static void generate(String ontologyDirectory, String alignerName, AlignmentProcess aligner) {
        String currentDirectory = System.getProperty("user.dir");

        URI onto1 = null;
        URI onto2 = null;
        Properties params = new Properties();

        try {
            onto1 = new URI(String.format("file://%s/%s", currentDirectory, ETHICS_ONTOLOGOY_DIRECTORY));
            onto2 = new URI(String.format("file://%s/%s", currentDirectory, ontologyDirectory));

            aligner.init ( onto1, onto2 );
            aligner.align( (Alignment)null, params );

            FileOutputStream file = new FileOutputStream(
                    String.format("resources/%s_%s.owl", FilenameUtils.getBaseName(ontologyDirectory), alignerName));

            PrintWriter writer = new PrintWriter(file, true);

            AlignmentVisitor renderer = new OWLAxiomsRendererVisitor(writer);
            aligner.render(renderer);
            writer.flush();
            writer.close();

        } catch (Exception e) { e.printStackTrace(); };
    }
}
