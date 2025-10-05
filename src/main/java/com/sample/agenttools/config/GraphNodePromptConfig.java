package com.sample.agenttools.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "spring.graph-nodes")
@Getter
@Setter
public class GraphNodePromptConfig {
    private Map<String, NodeConfig> nodes;

    public record NodeConfig(String promptId) {}

    public String getPromptIdForNode(String nodeName) {
        NodeConfig config = nodes != null ? nodes.get(nodeName) : null;
        return config != null ? config.promptId() : null;
    }
}
