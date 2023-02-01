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
import java.util.Properties;

import static ie.tcd.mcardleg.RiskyLinkBackend.Constants.ETHICS_ONTOLOGOY_DIRECTORY;


public class AlignmentGenerator {

    public static void generate(String alignmentDirectory) {
        String currentDirectory = System.getProperty("user.dir");

        URI onto1 = null;
        URI onto2 = null;
        Properties params = new Properties();

        try {
            onto1 = new URI(String.format("file://%s/%s", currentDirectory, ETHICS_ONTOLOGOY_DIRECTORY));
            onto2 = new URI(String.format("file://%s/%s", currentDirectory, alignmentDirectory));

            AlignmentProcess a1 = new StringDistAlignment();
            a1.init ( onto1, onto2 );
            a1.align( (Alignment)null, params );

            // Creates a FileOutputStream
            FileOutputStream file = new FileOutputStream("output.txt");

            // Creates a PrintWriter
            PrintWriter writer = new PrintWriter(file, true);

//            PrintWriter writer = new PrintWriter (
//                    new BufferedWriter(
//                            new OutputStreamWriter( System.out, StandardCharsets.UTF_8.name() )), true);

//            File file = new File (RESULTS_DIRECTORY, filename);
//            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            AlignmentVisitor renderer = new OWLAxiomsRendererVisitor(writer);
            a1.render(renderer);
            writer.flush();
            writer.close();

        } catch (Exception e) { e.printStackTrace(); };
    }
}
