package com.sample.agenttools.services.ingestion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.agenttools.data.ingestion.CveEmbeddingDto;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

@Slf4j
public class CveIngestionService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String schemaPath = "data/CVE_Record_Format.json";

    public CveEmbeddingDto ingestCveDocument(String fileName) {
        log.info("Starting ingestion for CVE document: {}", fileName);
        try {
            // Load schema
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
            JsonSchema schema = factory.getSchema(Files.newInputStream(Paths.get(schemaPath)));
            // Load CVE JSON
            JsonNode cveNode = objectMapper.readTree(new File(fileName));
            // Validate
            log.info("Validating CVE document against schema...");
            var errors = schema.validate(cveNode);
            if (!errors.isEmpty()) {
                log.error("Schema validation failed for {}: {}", fileName, errors);
                throw new IllegalArgumentException("CVE document does not comply with schema: " + errors);
            }
            log.info("Schema validation passed for {}", fileName);
            // Extract fields
            CveEmbeddingDto dto = new CveEmbeddingDto();
            dto.setCveId(cveNode.path("cveMetadata").path("cveId").asText());
            dto.setTitle(cveNode.path("containers").path("cna").path("title").asText());
            // Description (English)
            JsonNode descriptions = cveNode.path("containers").path("cna").path("descriptions");
            String description = "";
            for (JsonNode desc : descriptions) {
                if (desc.path("lang").asText().startsWith("en")) {
                    description = desc.path("value").asText();
                    break;
                }
            }
            dto.setDescription(description);
            dto.setDatePublished(cveNode.path("cveMetadata").path("datePublished").asText());
            dto.setDateUpdated(cveNode.path("cveMetadata").path("dateUpdated").asText());
            // References
            List<String> references = new ArrayList<>();
            JsonNode refs = cveNode.path("containers").path("cna").path("references");
            for (JsonNode ref : refs) {
                references.add(ref.path("url").asText());
            }
            dto.setReferences(references);
            // Program files
            List<String> programFiles = new ArrayList<>();
            JsonNode affected = cveNode.path("containers").path("cna").path("affected");
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
            JsonNode cpeApplicability = cveNode.path("containers").path("cna").path("cpeApplicability");
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
            log.info("Successfully ingested CVE document: {}", fileName);
            return dto;
        } catch (Exception e) {
            log.error("Error ingesting CVE document {}: {}", fileName, e.getMessage(), e);
            throw new RuntimeException("Failed to ingest CVE document: " + fileName, e);
        }
    }
}

