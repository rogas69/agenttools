package com.sample.agenttools.data.ingestion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CveJsonProcessorService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CveEmbeddingDto extractCveEmbedding(String filePath) {
        try {
            JsonNode root = objectMapper.readTree(new File(filePath));
            CveEmbeddingDto dto = new CveEmbeddingDto();

            // CVE ID
            dto.setCveId(root.path("cveMetadata").path("cveId").asText());
            // Title
            dto.setTitle(root.path("containers").path("cna").path("title").asText());
            // Description (English)
            JsonNode descriptions = root.path("containers").path("cna").path("descriptions");
            String description = "";
            for (JsonNode desc : descriptions) {
                if (desc.path("lang").asText().startsWith("en")) {
                    description = desc.path("value").asText();
                    break;
                }
            }
            dto.setDescription(description);
            // Dates
            dto.setDatePublished(root.path("cveMetadata").path("datePublished").asText());
            dto.setDateUpdated(root.path("cveMetadata").path("dateUpdated").asText());
            // References
            List<String> references = new ArrayList<>();
            JsonNode refs = root.path("containers").path("cna").path("references");
            for (JsonNode ref : refs) {
                references.add(ref.path("url").asText());
            }
            dto.setReferences(references);
            // Program files
            List<String> programFiles = new ArrayList<>();
            JsonNode affected = root.path("containers").path("cna").path("affected");
            for (JsonNode prod : affected) {
                JsonNode files = prod.path("programFiles");
                for (JsonNode file : files) {
                    programFiles.add(file.asText());
                }
            }
            dto.setProgramFiles(programFiles);
            // Affected products
            List<CveEmbeddingDto.AffectedProduct> affectedProducts = new ArrayList<>();
            for (JsonNode prod : affected) {
                CveEmbeddingDto.AffectedProduct ap = new CveEmbeddingDto.AffectedProduct();
                ap.setVendor(prod.path("vendor").asText());
                ap.setProduct(prod.path("product").asText());
                ap.setDefaultStatus(prod.path("defaultStatus").asText());
                // Versions
                List<CveEmbeddingDto.VersionStatus> versions = new ArrayList<>();
                JsonNode vers = prod.path("versions");
                for (JsonNode v : vers) {
                    CveEmbeddingDto.VersionStatus vs = new CveEmbeddingDto.VersionStatus();
                    vs.setVersion(v.path("version").asText());
                    vs.setStatus(v.path("status").asText());
                    vs.setVersionType(v.path("versionType").asText(null));
                    vs.setLessThan(v.path("lessThan").asText(null));
                    vs.setLessThanOrEqual(v.path("lessThanOrEqual").asText(null));
                    versions.add(vs);
                }
                ap.setVersions(versions);
                affectedProducts.add(ap);
            }
            dto.setAffectedProducts(affectedProducts);
            // CPE Applicability
            List<CveEmbeddingDto.CpeApplicability> cpeList = new ArrayList<>();
            JsonNode cpeApplicability = root.path("containers").path("cna").path("cpeApplicability");
            for (JsonNode elem : cpeApplicability) {
                JsonNode nodes = elem.path("nodes");
                for (JsonNode node : nodes) {
                    JsonNode cpeMatch = node.path("cpeMatch");
                    for (JsonNode match : cpeMatch) {
                        CveEmbeddingDto.CpeApplicability cpe = new CveEmbeddingDto.CpeApplicability();
                        cpe.setCriteria(match.path("criteria").asText());
                        cpe.setVulnerable(match.path("vulnerable").asBoolean());
                        cpe.setVersionStartIncluding(match.path("versionStartIncluding").asText(null));
                        cpe.setVersionEndExcluding(match.path("versionEndExcluding").asText(null));
                        cpe.setVersionEndIncluding(match.path("versionEndIncluding").asText(null));
                        cpeList.add(cpe);
                    }
                }
            }
            dto.setCpeApplicability(cpeList);
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Failed to process CVE JSON: " + e.getMessage(), e);
        }
    }
}
