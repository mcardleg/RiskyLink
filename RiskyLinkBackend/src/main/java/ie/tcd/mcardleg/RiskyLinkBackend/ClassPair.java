package ie.tcd.mcardleg.RiskyLinkBackend;

import java.util.Objects;

public class ClassPair {

    private String demographic;
    private String sensitiveInfo;

    public ClassPair(String demographic, String sensitiveInfo) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassPair that = (ClassPair) o;
        return getDemographic().equals(that.getDemographic()) && getSensitiveInfo().equals(that.getSensitiveInfo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDemographic(), getSensitiveInfo());
    }
}
