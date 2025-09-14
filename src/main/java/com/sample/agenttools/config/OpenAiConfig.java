package com.sample.agenttools.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.ai.openai")
public class OpenAiConfig {
    private String apiKey;
    private String baseUrl;
    private Chat chat = new Chat();

    @Getter
    @Setter
    public static class Chat {
        private String model;
        private Options options = new Options();
    }

    @Getter
    @Setter
    public static class Options {
        private int maxTokens;
    }
}
