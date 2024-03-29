package ie.tcd.mcardleg.RiskyLinkBackend;

import java.util.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.io.FileReader;

import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class DBHandler {
    public static final String ETHICS_ONTOLOGOY_DIRECTORY = "src/main/resources/risky_link_ethics.ttl";
    private HashMap<String, RepositoryConnection> activeRepos = new HashMap<>();
    private HashMap<String, HashMap<String, HashMap<String, List<Triple>>>> queryResults = new HashMap<>();
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
        return true;
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

    public Set<ClassPair> runQueries(String sessionId) {
        Set<ClassPair> categories = new HashSet<ClassPair>();
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject)parser.parse(new FileReader(QUERIES_DIRECTORY));
            JSONArray queries = (JSONArray)jsonObject.get("queries");
            Iterator queriesIterator = queries.iterator();
            while (queriesIterator.hasNext()) {
                JSONObject object = (JSONObject)queriesIterator.next();
                categories = query(sessionId, readInQuery(object), categories);
            }
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("Queries have been run.");

        return categories;
    }

    public List<Triple> getLinks(String sessionId, String demographic, String sensitiveInfo) {
        log.info(String.format("Session:%s requested the links between %s and %s.", sessionId, demographic, sensitiveInfo));
        return queryResults.get(sessionId).get(demographic).get(sensitiveInfo);
    }

    public void endSession(String sessionId) {
        if (activeRepos.containsKey(sessionId)) {
            activeRepos.get(sessionId).close();
            activeRepos.remove(sessionId);
        }
        if (queryResults.containsKey(sessionId)) {
            queryResults.remove(sessionId);
        }
        log.info("Session torn down.");
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

    private Boolean checkSessionExists(String sessionId) {
        if (activeRepos.containsKey(sessionId) && activeRepos.get(sessionId).isOpen()) {
            return true;
        }
        return false;
    }

    private void uploadFile(String sessionId, String filePath, RDFFormat format, Boolean deleteAfter) {
        try {
            log.info("Uploaded " + filePath);
            activeRepos.get(sessionId).add(new File(filePath), baseURI, format);
        }
        catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readInQuery(JSONObject object) {
        JSONArray queryLines = (JSONArray)object.get("query");
        Iterator linesIterator = queryLines.iterator();
        String queryString = "";
        while (linesIterator.hasNext()) {
            queryString += "\n" + linesIterator.next().toString();
        }
        return queryString;
    }

    private Set<ClassPair> query(String sessionId, String queryString, Set<ClassPair> categories) {
        TupleQuery tupleQuery = activeRepos.get(sessionId).prepareTupleQuery(queryString);
        TupleQueryResult result = tupleQuery.evaluate();
        HashMap<String, HashMap<String, List<Triple>>> tempMap1 = new HashMap<>();
        HashMap<String, List<Triple>> tempMap2 = new HashMap<>();
        List<Triple> tempList = new ArrayList<>();

        String sensitiveInfo, demographic, subject, predicate, object;

        while (result.hasNext()) {
            BindingSet bindingSet = result.next();
            demographic = bindingSet.getValue(DEMOGRAPHIC_FIELD).toString();
            sensitiveInfo = bindingSet.getValue(SENSITIVE_INFO_FIELD).toString();
            subject = bindingSet.getValue(SUBJECT_FIELD).toString();
            predicate = bindingSet.getValue(PREDICATE_FIELD).toString();
            object = bindingSet.getValue(OBJECT_FIELD).toString();

            categories.add(new ClassPair(demographic, sensitiveInfo));

            if (queryResults.containsKey(sessionId)) {
                tempMap1 = queryResults.get(sessionId);

                if (tempMap1.containsKey(demographic)) {
                    tempMap2 = tempMap1.get(demographic);

                    if (tempMap2.containsKey(sensitiveInfo)) {
                        tempList = tempMap2.get(sensitiveInfo);
                        tempList.add(new Triple(subject, predicate, object));
                    }
                }
            }
            tempMap2.put(sensitiveInfo, tempList);
            tempMap1.put(demographic, tempMap2);
            queryResults.put(sessionId, tempMap1);
        }
        result.close();

        return categories;
    }
    
}
