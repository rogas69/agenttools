package com.sample.agenttools.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class DateTimeTools {
    /**
     * Returns the current date and time as an ISO-8601 string.
     * This tool can be used by Spring AI models to retrieve the current time.
     */
    @Tool(name = "getCurrentTime", description = "Returns the current date and time as an ISO-8601 string.")
    public String getCurrentTime() {
        return LocalDateTime.now().toString();
    }
}
