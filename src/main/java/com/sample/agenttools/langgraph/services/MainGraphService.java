package com.sample.agenttools.langgraph.services;

import com.sample.agenttools.langgraph.MainGraphSerializer;
import com.sample.agenttools.langgraph.MainGraphState;
import com.sample.agenttools.langgraph.chatflow.GreeterNode;
import com.sample.agenttools.langgraph.chatflow.ResponderNode;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.StateGraph;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.List;
import java.util.Map;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@Service
@Slf4j
public class MainGraphService {
    private CompiledGraph<MainGraphState> compiledGraph;

    private final GreeterNode greeterNode;
    private final ResponderNode responderNode;

    public MainGraphService(
            GreeterNode greeterNode,
            ResponderNode responderNode
    ) {
        this.greeterNode = greeterNode;
        this.responderNode = responderNode;

        log.info("MainGraphService initialized");
    }

    public StateGraph<MainGraphState> configureGraph(){
        try {
            var stateGraph = new StateGraph<MainGraphState>(MainGraphState.SCHEMA, new MainGraphSerializer())
                    .addNode("greeter", node_async(greeterNode))
                    .addNode("responder", node_async(responderNode))
                    // Define edges
                    .addEdge(START, "greeter") // Start with the greeter node
                    .addEdge("greeter", "responder")
                    .addEdge("responder", END)   // End after the responder node
                    ;
            log.info("Graph configured successfully");
            return stateGraph;
        } catch (GraphStateException e) {
            log.error("Error configuring graph: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Object> createInitialState(
            List<Message> chatHistory,
            String conversationId,
            String userId
    ) {
        return MainGraphState.createInitialState(chatHistory, conversationId, userId);
    }
}
