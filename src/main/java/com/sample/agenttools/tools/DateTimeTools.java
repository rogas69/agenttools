package com.sample.agenttools.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Slf4j
@Component
public class DateTimeTools {
    /**
     * Returns the current date and time as an ISO-8601 string.
     * This tool can be used by Spring AI models to retrieve the current time.
     * This tool is required to get the current date that is used by the LLM to answer questions about current events.
     * The current date must be recorded in the chat history for the LLM to use it.
     * In other case it considers its training cut-off date as current date.
     */
    @Tool(name = "getCurrentTime", description = "Return the current date and time as an ISO-8601 string.")
    public String getCurrentTime() {
        log.info("Getting current date and time");
        return LocalDateTime.now().toString();
    }
    /**
     * Calculates the difference in days between two dates (ISO-8601 format).<br/>
     * Sample questions:<br/>
     * "How many days passed since the breakout of word war two?"<br/>
     * "How many days passed between 12 september 2001 and today?"
     * @param startDate ISO-8601 formatted start date (e.g., "2025-09-01")
     * @param endDate ISO-8601 formatted end date (e.g., "2025-09-16")
     * @return The number of days between startDate and endDate
     */
    @Tool(name = "calculateDaysBetween",
            description = "Calculate the difference in days between two ISO-8601 dates. " +
    "Note: If the question indicates that the present date (today) should be used, ALWAYS check what date and time it is." +
                    "Example question: What is the number of days between 2023-09-01 and today? " +
    "Provide current date in the response if needed.")
//    @Tool(
//            name = "calculateDaysBetween",
//            description = "Calculate the difference in days between two ISO-8601 dates. If the end date is 'today' or 'current date', first call the getCurrentTime tool to get the current date, then use it as the end date. " +
//                          "Provide current date in the response if needed."
//    )
    public long calculateDaysBetween(String startDate, String endDate) {
        java.time.LocalDate start = java.time.LocalDate.parse(startDate);
        java.time.LocalDate end = java.time.LocalDate.parse(endDate);
        log.info("Calculating days between {} and {}", start, end);
        return java.time.temporal.ChronoUnit.DAYS.between(start, end);
    }
}
