package ie.tcd.mcardleg.RiskyLinkBackend;

public class CategoryPair {

    private String demographic;
    private String sensitiveInfo;

    public CategoryPair(String demographic, String sensitiveInfo) {
        this.demographic = demographic;
        this.sensitiveInfo = sensitiveInfo;
    }

    public String getDemographic() {
        return demographic;
    }

    public void setDemographic(String demographic) {
        this.demographic = demographic;
    }

    public String getSensitiveInfo() {
        return sensitiveInfo;
    }

    public void setSensitiveInfo(String sensitiveInfo) {
        this.sensitiveInfo = sensitiveInfo;
    }

    @Override
    public String toString() {
        return "CategoryPair{" +
                "demographic='" + demographic + '\'' +
                ", sensitiveInfo='" + sensitiveInfo + '\'' +
                '}';
    }
}
