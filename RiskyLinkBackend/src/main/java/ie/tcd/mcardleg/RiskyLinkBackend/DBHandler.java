package ie.tcd.mcardleg.RiskyLinkBackend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.io.FileReader;

import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import static ie.tcd.mcardleg.RiskyLinkBackend.Constants.ETHICS_ONTOLOGOY_DIRECTORY;

public class DBHandler {

    private HashMap<String, RepositoryConnection> activeRepos = new HashMap<>();
    private String baseURI = "http://www.semanticweb.org/riskylink";
    private Logger log = LoggerFactory.getLogger(DBHandler.class);
    private final String QUERIES_DIRECTORY = "src/main/resources/sparql_queries.json";
    private final String SENSITIVE_INFO_FIELD = "types_of_sensitive_info";
    private final String DEMOGRAPHIC_FIELD = "demographic";
    private final String SUBJECT_FIELD = "subject";
    private final String PREDICATE_FIELD = "predicate";
    private final String OBJECT_FIELD = "object";

    public void addDataset(String sessionId, Path path) {
        if (!checkRepositoryExists(sessionId)) {
            setUpRepository(sessionId);
        }
        uploadFile(sessionId, path.toString(), RDFFormat.TURTLE);
    }

    public void addOntology(String sessionId, Path path) {
        if (!checkRepositoryExists(sessionId)) {
            setUpRepository(sessionId);
        }
        uploadFile(sessionId, path.toString(), RDFFormat.TURTLE);

        for (String alignmentPath : AlignmentGenerator.runGenerator(path.toString())){
            uploadFile(sessionId, alignmentPath, RDFFormat.RDFXML);
        }
    }

    public HashMap<String, List<QueryResult>> runQueries(String sessionId) {
        HashMap<String, List<QueryResult>> results = new HashMap<String, List<QueryResult>>();
        log.debug(String.valueOf(checkRepositoryExists(sessionId)));
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject)parser.parse(
                    new FileReader(QUERIES_DIRECTORY));
            JSONArray queries = (JSONArray)jsonObject.get("queries");
            Iterator queriesIterator = queries.iterator();
            while (queriesIterator.hasNext()) {
                JSONObject object = (JSONObject)queriesIterator.next();
                String queryName = (String)object.get("query_name");
                log.debug(queryName);
                JSONArray queryLines = (JSONArray)object.get("query");
                Iterator linesIterator = queryLines.iterator();
                String queryString = "";
                while (linesIterator.hasNext()) {
                    queryString += "\n" + linesIterator.next().toString();
                }
                results.put(queryName, query(sessionId, queryString));
            }
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("Queries have been run.");

        return results;
    }
    
    public void tearDownDB(String sessionId) {
        if (activeRepos.containsKey(sessionId)) {
            activeRepos.get(sessionId).close();
            activeRepos.remove(sessionId);
        }
        log.info("Repo connection closed.");
    }

    // Utils
    private void setUpRepository(String sessionId) {
        File dataDir = new File(sessionId + "/");
        Repository repo = new SailRepository(new NativeStore(dataDir));
        activeRepos.put(sessionId, repo.getConnection());
        uploadFile(sessionId, ETHICS_ONTOLOGOY_DIRECTORY, RDFFormat.TURTLE);
        log.info("Set up repo");
    }

    private boolean checkRepositoryExists(String sessionId) {
        if (activeRepos.containsKey(sessionId) && activeRepos.get(sessionId).isOpen()) {
            log.info("Repo exists");
            return true;
        }
        log.info("Repo does not exist");
        return false;
    }

    private void uploadFile(String sessionId, String filePath, RDFFormat format) {
        try {
            log.info("Uploaded " + filePath);
            activeRepos.get(sessionId).add(new File(filePath), baseURI, format);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<QueryResult> query(String sessionId, String queryString) {
        TupleQuery tupleQuery = activeRepos.get(sessionId).prepareTupleQuery(queryString);
        TupleQueryResult result = tupleQuery.evaluate();
        List<QueryResult> queryResults = new ArrayList<QueryResult>();

        while (result.hasNext()) {  // iterate over the result
            BindingSet bindingSet = result.next();
            QueryResult queryResult = new QueryResult(
                    bindingSet.getValue(SENSITIVE_INFO_FIELD),
                    bindingSet.getValue(DEMOGRAPHIC_FIELD),
                    bindingSet.getValue(SUBJECT_FIELD),
                    null, null);

//                    bindingSet.getValue(PREDICATE_FIELD),
//                    bindingSet.getValue(OBJECT_FIELD));
//            log.debug(bindingSet.getValue(SUBJECT_FIELD).toString());
            queryResults.add(queryResult);
        }
        result.close();
        return queryResults;
    }
    
}
