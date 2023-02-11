package ie.tcd.mcardleg.RiskyLinkBackend;

public class QueryResult {

    private String demographic;
    private String data;
    private String equivalentClass;

    public QueryResult(String demographic, String data, String equivalentClass) {
        this.demographic = demographic;
        this.data = data;
        this.equivalentClass = equivalentClass;
    }

    @Override
    public String toString() {
        return "QueryResult{" +
                "demographic='" + demographic + '\'' +
                ", data='" + data + '\'' +
                ", equivalentClass='" + equivalentClass + '\'' +
                '}';
    }
}
