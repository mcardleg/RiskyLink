package ie.tcd.mcardleg.RiskyLinkBackend;

import org.eclipse.rdf4j.model.Value;

public class QueryResult {

    private String sensitiveInfo;
    private String demographic;
    private String subject;
    private String predicate;
    private String object;

    public QueryResult(Value sensitiveInfo, Value demographic,
                       Value subject, Value predicate, Value object) {
        this.sensitiveInfo = sensitiveInfo.toString();
        this.demographic = demographic.toString();
        this.subject = subject.toString();
        this.predicate = predicate.toString();
        this.object = object.toString();
    }

    public String getSensitiveInfo() {
        return sensitiveInfo;
    }

    public void setSensitiveInfo(String sensitiveInfo) {
        this.sensitiveInfo = sensitiveInfo;
    }

    public String getDemographic() {
        return demographic;
    }

    public void setDemographic(String demographic) {
        this.demographic = demographic;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    @Override
    public String toString() {
        return "QueryResult{" +
                "sensitiveInfo='" + sensitiveInfo + '\'' +
                ", demographic='" + demographic + '\'' +
                ", subject='" + subject + '\'' +
                ", predicate='" + predicate + '\'' +
                ", object='" + object + '\'' +
                '}';
    }

}


