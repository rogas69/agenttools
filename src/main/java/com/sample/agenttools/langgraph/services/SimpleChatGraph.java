package com.sample.agenttools.langgraph.services;

import com.sample.agenttools.langgraph.MainGraphState;
import lombok.extern.slf4j.Slf4j;
import org.bsc.async.AsyncGenerator;
import org.bsc.langgraph4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sample.agenttools.langgraph.chatflow.GreeterNode;
import com.sample.agenttools.langgraph.chatflow.ResponderNode;

import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.StateGraph.END;
import java.util.Map;

@Service
@Slf4j
/**
 * Builds a simple chat graph with two nodes: GreeterNode and ResponderNode.
 * The GreeterNode adds a greeting message, and the ResponderNode responds to it.
 */
public class SimpleChatGraph {
    private final GreeterNode greeterNode;
    private final ResponderNode responderNode;

    private CompiledGraph<MainGraphState> compiledGraph;

    @Autowired
    public SimpleChatGraph(
            GreeterNode greeterNode,
            ResponderNode responderNode
    ) {
        this.greeterNode = greeterNode;
        this.responderNode = responderNode;

        configureGraph();

        this.compiledGraph.stream( Map.of( MainGraphState.CHAT_HISTORY, "Let's, begin!" ));

        log.info("SimpleChatGraph initialized");
    }

    private void configureGraph(){
        try {
            var stateGraph = new StateGraph<>(MainGraphState.SCHEMA, initData -> new MainGraphState(initData))
                    .addNode("greeter", node_async(greeterNode))
                    .addNode("responder", node_async(responderNode))
                    // Define edges
                    .addEdge(START, "greeter") // Start with the greeter node
                    .addEdge("greeter", "responder")
                    .addEdge("responder", END)   // End after the responder node
                    ;

            // Compile the graph
            this.compiledGraph = stateGraph.compile();
        } catch (GraphStateException e) {
            log.error("Error configuring graph: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public AsyncGenerator<NodeOutput<MainGraphState>> stream(Map<String, Object> inputs) {
        return this.compiledGraph.stream(inputs);
    }
}
