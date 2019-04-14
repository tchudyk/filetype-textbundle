package pl.codeset.textbundle;

import com.google.gson.annotations.SerializedName;

import java.util.Map;
import java.util.Objects;

public class MetaData {

    private Integer version;
    private String type;
    @SerializedName("transient")
    private Boolean isTransient;
    private String creatorURL;
    private String creatorIdentifier;
    private String sourceURL;
    private Map<String, Object> applicationContent;

    public MetaData() {
        this.version = 2;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getTransient() {
        return isTransient;
    }

    public void setTransient(Boolean aTransient) {
        isTransient = aTransient;
    }

    public String getCreatorURL() {
        return creatorURL;
    }

    public void setCreatorURL(String creatorURL) {
        this.creatorURL = creatorURL;
    }

    public String getCreatorIdentifier() {
        return creatorIdentifier;
    }

    public void setCreatorIdentifier(String creatorIdentifier) {
        this.creatorIdentifier = creatorIdentifier;
    }

    public String getSourceURL() {
        return sourceURL;
    }

    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }

    public Map<String, Object> getApplicationContent() {
        return applicationContent;
    }

    public void setApplicationContent(Map<String, Object> applicationContent) {
        this.applicationContent = applicationContent;
    }

    @Override
    public String toString() {
        return "MetaData{" +
                "version=" + version +
                ", type='" + type + '\'' +
                ", isTransient=" + isTransient +
                ", creatorURL='" + creatorURL + '\'' +
                ", creatorIdentifier='" + creatorIdentifier + '\'' +
                ", sourceURL='" + sourceURL + '\'' +
                ", applicationContent=" + applicationContent +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetaData metaData = (MetaData) o;
        return Objects.equals(version, metaData.version) &&
                Objects.equals(type, metaData.type) &&
                Objects.equals(isTransient, metaData.isTransient) &&
                Objects.equals(creatorURL, metaData.creatorURL) &&
                Objects.equals(creatorIdentifier, metaData.creatorIdentifier) &&
                Objects.equals(sourceURL, metaData.sourceURL) &&
                Objects.equals(applicationContent, metaData.applicationContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, type, isTransient, creatorURL, creatorIdentifier, sourceURL, applicationContent);
    }
}
