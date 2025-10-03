package com.sample.agenttools.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.files")
@Data
public class FileStorageProperties {
    private Cve cve = new Cve("");
    private Policies policies = new Policies("");
    private Uploads uploads = new Uploads("");

    public record Cve(String path) {}
    public record Policies(String path) {}
    public record Uploads(String path) {}
}
