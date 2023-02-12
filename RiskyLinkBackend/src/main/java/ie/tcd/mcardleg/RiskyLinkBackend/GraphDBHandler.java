package ie.tcd.mcardleg.RiskyLinkBackend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GraphDBHandler {
    private Logger log = LoggerFactory.getLogger(GraphDBHandler.class);
//    private String baseURI = "";
    private String baseURI = "http://www.semanticweb.org/riskylink";
    private Repository repo;
    private RepositoryConnection connection;

    public void addDataset(String filePath) {
        if (!checkRepositoryExists()) {
            setUpDB();
        }
        uploadFile(filePath, RDFFormat.TURTLE);
    }

    public void addOntology(Path path) {
        if (!checkRepositoryExists()) {
            setUpDB();
        }
        uploadFile(path.toString(), RDFFormat.TURTLE);

        for (String alignmentPath : AlignmentGenerator.runGenerator(path.toString())){
            uploadFile(alignmentPath, RDFFormat.RDFXML);
        }
    }

    private boolean checkRepositoryExists() {
        if (connection == null) {
            return false;
        }
        return connection.isOpen();
    }

    private void setUpDB() {
        repo = new SailRepository(new NativeStore());
        connection = repo.getConnection();
        log.info("Database connection acquired.");
    }

    private void uploadFile(String filePath, RDFFormat format) {
        try {
            connection.add(new File(filePath), baseURI, format);
            log.info("Uploaded " + filePath);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void tearDownDB() {
        connection.close();
        log.info("Database connection closed.");
    }

    public List<List<QueryResult>> runQueries() {
        List<List<QueryResult>> results = new ArrayList<List<QueryResult>>();

        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject)parser.parse(
                    new FileReader("src/main/resources/sparql_queries.json"));
            JSONArray queries = (JSONArray)jsonObject.get("queries");
            Iterator queriesIterator = queries.iterator();
            while (queriesIterator.hasNext()) {
                JSONArray queryLines = (JSONArray)queriesIterator.next();
                Iterator linesIterator = queryLines.iterator();
                String queryString = "";
                while (linesIterator.hasNext()) {
                    queryString += "\n" + linesIterator.next().toString();
                }
                results.add(query(queryString));
            }
        } catch(IOException | ParseException e) {
            log.error(e.getMessage(), e);
        }
        log.info("Queries have been run.");

        return results;
    }

    private List<QueryResult> query(String queryString) {
        System.out.println(queryString);
        TupleQuery tupleQuery = connection.prepareTupleQuery(queryString);
        TupleQueryResult result = tupleQuery.evaluate();
        List<QueryResult> queryResults = new ArrayList<QueryResult>();

        while (result.hasNext()) {  // iterate over the result
            System.out.println("reached");
            BindingSet bindingSet = result.next();
//            Value demographic = bindingSet.getValue("demographic");
//            Value data = bindingSet.getValue("data");
            Value equivilent_classes = bindingSet.getValue("equivilent_classes");
            QueryResult queryResult = new QueryResult(
//                    bindingSet.getValue("demographic").toString(),
//                    bindingSet.getValue("data").toString(),
                    null, null,
                    bindingSet.getValue("equivilent_classes").toString());
            System.out.println(queryResult.toString());
            queryResults.add(queryResult);
        }
        result.close();
        return queryResults;
    }
}
