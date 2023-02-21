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
//        this.sensitiveInfo = sensitiveInfo.toString();
//        this.demographic = demographic.toString();
        this.sensitiveInfo = null;
        this.demographic = null;
        this.subject = subject.toString();
        this.predicate = predicate.toString();
        this.object = object.toString();
    }

    public String getSensitiveInfo() {
        return sensitiveInfo;
    }

    public void setSensitiveInfo(Value sensitiveInfo) {
        this.sensitiveInfo = sensitiveInfo.toString();
    }

    public String getDemographic() {
        return demographic;
    }

    public void setDemographic(Value demographic) {
        this.demographic = demographic.toString();
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(Value subject) {
        this.subject = subject.toString();
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(Value predicate) {
        this.predicate = predicate.toString();
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


