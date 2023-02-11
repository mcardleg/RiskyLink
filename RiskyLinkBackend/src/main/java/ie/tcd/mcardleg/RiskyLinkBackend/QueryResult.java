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

    public String getDemographic() {
        return demographic;
    }

    public void setDemographic(String demographic) {
        this.demographic = demographic;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getEquivalentClass() {
        return equivalentClass;
    }

    public void setEquivalentClass(String equivalentClass) {
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
