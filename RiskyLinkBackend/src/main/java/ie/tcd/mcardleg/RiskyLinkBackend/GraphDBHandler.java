package ie.tcd.mcardleg.RiskyLinkBackend;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GraphDBHandler {
    private static Logger log = LoggerFactory.getLogger(GraphDBHandler.class);

    public static void uploadDataset(String filename) {
//        InputStream input = GraphDBHandler.class.getResourceAsStream("/councillor_salaries.ttl");
        InputStream input = GraphDBHandler.class.getResourceAsStream("/" + filename);
        log.debug(input.toString());
//        try {
//            Model model = Rio.parse(input, "", RDFFormat.TURTLE);
//            model.forEach(System.out::println);
//        } catch (IOException e) {
//            log.error(e.getMessage(), e);
//        }
    }
}
