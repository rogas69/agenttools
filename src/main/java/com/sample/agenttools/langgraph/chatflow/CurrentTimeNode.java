package com.sample.agenttools.langgraph.chatflow;

import com.sample.agenttools.langgraph.MainGraphState;
import org.bsc.langgraph4j.action.NodeAction;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * A node that just adds the current time to the state. <br/>
 * Note that it can be done in the initial state if required only at the beginning of the execution of the graph.
 */
public class CurrentTimeNode implements NodeAction<MainGraphState> {

    @Override
    public Map<String, Object> apply(MainGraphState mainGraphState) throws Exception {
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        String formatted = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return Map.of("current_time", formatted);
    }
}
