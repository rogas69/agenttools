package com.sample.agenttools.langgraph.chatflow;

import com.sample.agenttools.config.GraphNodePromptConfig;
import com.sample.agenttools.services.JinjaTemplateService;
import com.sample.agenttools.services.operation.PromptService;
import com.sample.agenttools.langgraph.MainGraphState;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * A node that responds to time-based questions.
 * This node can be expanded to include logic for asking questions based on the time of day or other temporal factors.
 */
public class TimeBasedQuestionNode implements NodeAction<MainGraphState> {
    @Autowired
    private GraphNodePromptConfig graphNodePromptConfig;
    @Autowired
    private JinjaTemplateService jinja;
    @Autowired
    private PromptService promptService;

    @Override
    public Map<String, Object> apply(MainGraphState mainGraphState) throws Exception {
        var className = this.getClass()
                .getSimpleName();
        var promptTemplateId = graphNodePromptConfig.getPromptIdForNode(className);
        var prompt = promptService.getPromptById(promptTemplateId);
        var currentTime = mainGraphState
                .<String>value("current_time")
                .orElse("unknown time");
        var rendered = prompt
                .map(p -> jinja.renderString(p.getContent(), Map.of("current_time", currentTime)))
                .orElseThrow(() -> new RuntimeException("Prompt not found for id: " + promptTemplateId));

        //todo - add a call to chat with the rendered system prompt, chat history and the user question
        return Map.of();
    }
}
