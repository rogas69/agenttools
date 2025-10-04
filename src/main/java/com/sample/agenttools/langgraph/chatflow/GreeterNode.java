package com.sample.agenttools.langgraph.chatflow;

import com.sample.agenttools.langgraph.MainGraphState;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.stereotype.Component;

import java.util.Map;


// Node that adds a greeting

@Component
@Slf4j
public class GreeterNode implements NodeAction<MainGraphState> {
    @Override
    public Map<String, Object> apply(MainGraphState state) {
        log.info("GreeterNode executing. Current messages: {}", state.chatHistory());
        return Map.of(MainGraphState.CHAT_HISTORY, "Hello from GreeterNode!");
    }
}