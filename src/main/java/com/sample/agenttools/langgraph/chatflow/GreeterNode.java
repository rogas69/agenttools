package com.sample.agenttools.langgraph.chatflow;

import com.sample.agenttools.langgraph.ChatMemoryState;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.stereotype.Component;

import java.util.Map;


// Node that adds a greeting

@Component
@Slf4j
public class GreeterNode implements NodeAction<ChatMemoryState> {
    @Override
    public Map<String, Object> apply(ChatMemoryState state) {
        log.info("GreeterNode executing. Current messages: {}", state.messages());
        return Map.of(ChatMemoryState.MESSAGES_KEY, "Hello from GreeterNode!");
    }
}