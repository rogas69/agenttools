package com.sample.agenttools.langgraph.chatflow;

import com.sample.agenttools.langgraph.MainGraphState;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ResponderNode implements NodeAction<MainGraphState> {
    @Override
    public Map<String, Object> apply(MainGraphState state) {
        log.info("ResponderNode executing. Current messages: {}", state.chatHistory());
        List<String> currentMessages = state.chatHistory();
        if (currentMessages.contains("Hello from GreeterNode!")) {
            return Map.of(MainGraphState.CHAT_HISTORY, "Acknowledged greeting!");
        }
        return Map.of(MainGraphState.CHAT_HISTORY, "No greeting found.");
    }
}
