package ie.tcd.mcardleg.RiskyLinkBackend;

import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentProcess;
import org.semanticweb.owl.align.AlignmentVisitor;

import fr.inrialpes.exmo.align.impl.BasicAlignment;
import fr.inrialpes.exmo.align.impl.BasicParameters;
import fr.inrialpes.exmo.align.impl.method.StringDistAlignment;
import fr.inrialpes.exmo.align.impl.renderer.RDFRendererVisitor;

import org.xml.sax.SAXException;

import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.util.Properties;


public class Skeleton {

    public static void skel() {
        String[] args = new String[]{"file://$PWD/alignapi-version-4.10/examples/rdf/onto1.owl",
                "file://$PWD/alignapi-version-4.10/examples/rdf/onto2.owl"};

        URI onto1 = null;
        URI onto2 = null;
        Properties params = new Properties();

        try {
            // Loading ontologies
            if (args.length >= 2) {
                onto1 = new URI( args[0] );
                onto2 = new URI( args[1] );
            } else {
                System.err.println("Need two arguments to proceed");
                return ;
            }

            // Aligning
            AlignmentProcess a1 = new StringDistAlignment();
            a1.init ( onto1, onto2 );
            a1.align( (Alignment)null, params );

            // Outputing
            PrintWriter writer = new PrintWriter (
                    new BufferedWriter(
                            new OutputStreamWriter( System.out, StandardCharsets.UTF_8.name() )), true);
            AlignmentVisitor renderer = new RDFRendererVisitor(writer);
            a1.render(renderer);
            writer.flush();
            writer.close();
            System.out.println("reached");

        } catch (Exception e) { e.printStackTrace(); };
    }
}
