package ie.tcd.mcardleg.RiskyLinkBackend;

import org.eclipse.rdf4j.model.Value;

public class Triple {
    private String subject;
    private String predicate;
    private String object;

    public Triple(String subject, String predicate, String object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
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

    public String getObject() {
        return object;
    }

    public void setObject(Value object) {
        this.object = object.toString();
    }

    @Override
    public String toString() {
        return "QueryResult{" +
                "subject='" + subject + '\'' +
                ", predicate='" + predicate + '\'' +
                ", object='" + object + '\'' +
                '}';
    }
}
