package ie.tcd.mcardleg.RiskyLinkBackend;

import fr.inrialpes.exmo.align.impl.renderer.OWLAxiomsRendererVisitor;
import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentProcess;
import org.semanticweb.owl.align.AlignmentVisitor;

import fr.inrialpes.exmo.align.impl.BasicAlignment;
import fr.inrialpes.exmo.align.impl.BasicParameters;
import fr.inrialpes.exmo.align.impl.method.StringDistAlignment;
import fr.inrialpes.exmo.align.impl.renderer.RDFRendererVisitor;

import org.xml.sax.SAXException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Properties;
import org.apache.commons.io.FilenameUtils;

import static ie.tcd.mcardleg.RiskyLinkBackend.Constants.ETHICS_ONTOLOGOY_DIRECTORY;


public class AlignmentGenerator {

    public static void generate(String ontologyDirectory, AlignmentProcess aligner) {
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
                    String.format("resources/%s_alignment.ttl", FilenameUtils.getBaseName(ontologyDirectory)));

            PrintWriter writer = new PrintWriter(file, true);

            AlignmentVisitor renderer = new OWLAxiomsRendererVisitor(writer);
            aligner.render(renderer);
            writer.flush();
            writer.close();

        } catch (Exception e) { e.printStackTrace(); };
    }
}
