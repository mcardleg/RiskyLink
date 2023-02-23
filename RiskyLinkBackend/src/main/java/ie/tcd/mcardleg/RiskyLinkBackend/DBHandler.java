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

    public void ensureSessionSetUp(String sessionId){
        if (!checkSessionExists(sessionId)) {
            setUpSession(sessionId);
        }
    }

    public Boolean addDataset(String sessionId, Path path) {
        if (!checkSessionExists(sessionId)) {
            return false;
        }
        uploadFile(sessionId, path.toString(), RDFFormat.TURTLE, true);
        return false;
    }

    public Boolean addOntology(String sessionId, Path path) {
        if (!checkSessionExists(sessionId)) {
            return false;
        }

        uploadFile(sessionId, path.toString(), RDFFormat.TURTLE, true);
        for (String alignmentPath : AlignmentGenerator.runGenerator(sessionId, path.toString())) {
            uploadFile(sessionId, alignmentPath, RDFFormat.RDFXML, true);
        }
        return true;
    }

    public HashMap<String, List<QueryResult>> runQueries(String sessionId) {
        HashMap<String, List<QueryResult>> results = new HashMap<String, List<QueryResult>>();
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject)parser.parse(
                    new FileReader(QUERIES_DIRECTORY));
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
    private void setUpSession(String sessionId) {
        File tempDir = new File(FileHandlingUtils.generateTempDirectoryName(sessionId, ""));
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        Repository repo = new SailRepository(new NativeStore(new File(sessionId + "/")));
        activeRepos.put(sessionId, repo.getConnection());
        uploadFile(sessionId, ETHICS_ONTOLOGOY_DIRECTORY, RDFFormat.TURTLE, false);
        log.info("Set up session");
    }

    private boolean checkSessionExists(String sessionId) {
        if (activeRepos.containsKey(sessionId) && activeRepos.get(sessionId).isOpen()) {
            log.info("Repo exists");
            return true;
        }
        log.info("Repo does not exist");
        return false;
    }

    private void uploadFile(String sessionId, String filePath, RDFFormat format, Boolean deleteAfter) {
        try {
            log.info("Uploaded " + filePath);
            activeRepos.get(sessionId).add(new File(filePath), baseURI, format);

//            if (deleteAfter) {
//                String currentDirectory = System.getProperty("user.dir") + "/" + filePath;
//                log.info(currentDirectory);
//                File temp2 = new File(currentDirectory);
//                temp2.delete();
//            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<QueryResult> query(String sessionId, String queryString) {
        TupleQuery tupleQuery = activeRepos.get(sessionId).prepareTupleQuery(queryString);
        TupleQueryResult result = tupleQuery.evaluate();
        List<QueryResult> queryResults = new ArrayList<QueryResult>();

        while (result.hasNext()) {
            BindingSet bindingSet = result.next();
            QueryResult queryResult = new QueryResult(
                    bindingSet.getValue(SENSITIVE_INFO_FIELD),
                    bindingSet.getValue(DEMOGRAPHIC_FIELD),
//                    null, null,
                    bindingSet.getValue(SUBJECT_FIELD),
                    bindingSet.getValue(PREDICATE_FIELD),
                    bindingSet.getValue(OBJECT_FIELD));
            queryResults.add(queryResult);
        }
        result.close();
        return queryResults;
    }
    
}
