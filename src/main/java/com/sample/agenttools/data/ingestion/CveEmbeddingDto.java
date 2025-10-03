package com.sample.agenttools.data.ingestion;

import java.util.List;

public class CveEmbeddingDto {
    private String cveId;
    private String title;
    private String description;
    private List<AffectedProduct> affectedProducts;
    private List<String> programFiles;
    private List<CpeApplicability> cpeApplicability;
    private List<String> references;
    private String datePublished;
    private String dateUpdated;

    // Getters and setters

    public String getCveId() {
        return cveId;
    }

    public void setCveId(String cveId) {
        this.cveId = cveId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<AffectedProduct> getAffectedProducts() {
        return affectedProducts;
    }

    public void setAffectedProducts(List<AffectedProduct> affectedProducts) {
        this.affectedProducts = affectedProducts;
    }

    public List<String> getProgramFiles() {
        return programFiles;
    }

    public void setProgramFiles(List<String> programFiles) {
        this.programFiles = programFiles;
    }

    public List<CpeApplicability> getCpeApplicability() {
        return cpeApplicability;
    }

    public void setCpeApplicability(List<CpeApplicability> cpeApplicability) {
        this.cpeApplicability = cpeApplicability;
    }

    public List<String> getReferences() {
        return references;
    }

    public void setReferences(List<String> references) {
        this.references = references;
    }

    public String getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(String datePublished) {
        this.datePublished = datePublished;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public static class AffectedProduct {
        private String vendor;
        private String product;
        private List<VersionStatus> versions;
        private String defaultStatus;

        // Getters and setters

        public String getVendor() {
            return vendor;
        }

        public void setVendor(String vendor) {
            this.vendor = vendor;
        }

        public String getProduct() {
            return product;
        }

        public void setProduct(String product) {
            this.product = product;
        }

        public List<VersionStatus> getVersions() {
            return versions;
        }

        public void setVersions(List<VersionStatus> versions) {
            this.versions = versions;
        }

        public String getDefaultStatus() {
            return defaultStatus;
        }

        public void setDefaultStatus(String defaultStatus) {
            this.defaultStatus = defaultStatus;
        }
    }

    public static class VersionStatus {
        private String version;
        private String status;
        private String versionType;
        private String lessThan;
        private String lessThanOrEqual;

        // Getters and setters

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getVersionType() {
            return versionType;
        }

        public void setVersionType(String versionType) {
            this.versionType = versionType;
        }

        public String getLessThan() {
            return lessThan;
        }

        public void setLessThan(String lessThan) {
            this.lessThan = lessThan;
        }

        public String getLessThanOrEqual() {
            return lessThanOrEqual;
        }

        public void setLessThanOrEqual(String lessThanOrEqual) {
            this.lessThanOrEqual = lessThanOrEqual;
        }
    }

    public static class CpeApplicability {
        private String criteria;
        private boolean vulnerable;
        private String versionStartIncluding;
        private String versionEndExcluding;
        private String versionEndIncluding;

        // Getters and setters

        public String getCriteria() {
            return criteria;
        }

        public void setCriteria(String criteria) {
            this.criteria = criteria;
        }

        public boolean isVulnerable() {
            return vulnerable;
        }

        public void setVulnerable(boolean vulnerable) {
            this.vulnerable = vulnerable;
        }

        public String getVersionStartIncluding() {
            return versionStartIncluding;
        }

        public void setVersionStartIncluding(String versionStartIncluding) {
            this.versionStartIncluding = versionStartIncluding;
        }

        public String getVersionEndExcluding() {
            return versionEndExcluding;
        }

        public void setVersionEndExcluding(String versionEndExcluding) {
            this.versionEndExcluding = versionEndExcluding;
        }

        public String getVersionEndIncluding() {
            return versionEndIncluding;
        }

        public void setVersionEndIncluding(String versionEndIncluding) {
            this.versionEndIncluding = versionEndIncluding;
        }
    }
}
