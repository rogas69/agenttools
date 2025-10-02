package com.sample.agenttools.langgraph.chatflow;

import com.sample.agenttools.langgraph.ChatMemoryState;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ResponderNode implements NodeAction<ChatMemoryState> {
    @Override
    public Map<String, Object> apply(ChatMemoryState state) {
        log.info("ResponderNode executing. Current messages: {}", state.messages());
        List<String> currentMessages = state.messages();
        if (currentMessages.contains("Hello from GreeterNode!")) {
            return Map.of(ChatMemoryState.MESSAGES_KEY, "Acknowledged greeting!");
        }
        return Map.of(ChatMemoryState.MESSAGES_KEY, "No greeting found.");
    }
}
