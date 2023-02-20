package ie.tcd.mcardleg.RiskyLinkBackend;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

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
    private String baseURI = "http://www.semanticweb.org/riskylink";
    private Repository repo;
    private RepositoryConnection connection;

    public void addDataset(String sessionId, Path path) {
        if (!checkRepositoryExists()) {
            setUpDB();
        }
        uploadFile(path.toString(), RDFFormat.TURTLE);
    }

    public void addOntology(String sessionId, Path path) {
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

    public Map<String, List<QueryResult>> runQueries() {
        Map<String, List<QueryResult>> results = new HashMap<String, List<QueryResult>>();

        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject)parser.parse(
                    new FileReader("src/main/resources/sparql_queries.json"));
            JSONArray queries = (JSONArray)jsonObject.get("queries");
            Iterator queriesIterator = queries.iterator();
            while (queriesIterator.hasNext()) {
                JSONObject object = (JSONObject)queriesIterator.next();
                String queryName = (String)object.get("query_name");
                JSONArray queryLines = (JSONArray)object.get("query");
                Iterator linesIterator = queryLines.iterator();
                String queryString = "";
                while (linesIterator.hasNext()) {
                    queryString += "\n" + linesIterator.next().toString();
                }
                results.put(queryName, query(queryString));
            }
        } catch(IOException | ParseException e) {
            log.error(e.getMessage(), e);
        }
        log.info("Queries have been run.");

        return results;
    }

    private List<QueryResult> query(String queryString) {
        TupleQuery tupleQuery = connection.prepareTupleQuery(queryString);
        TupleQueryResult result = tupleQuery.evaluate();
        List<QueryResult> queryResults = new ArrayList<QueryResult>();

        while (result.hasNext()) {  // iterate over the result
            BindingSet bindingSet = result.next();
            QueryResult queryResult = new QueryResult(
//                    bindingSet.getValue("demographic").toString(),
//                    bindingSet.getValue("data").toString(),
                    null, null,
                    bindingSet.getValue("equivilent_classes").toString());
            queryResults.add(queryResult);
        }
        result.close();
        return queryResults;
    }
}
