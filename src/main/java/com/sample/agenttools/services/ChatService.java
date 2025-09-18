package com.sample.agenttools.services;

import com.sample.agenttools.api.model.operation.Message;
import com.sample.agenttools.config.OpenAiConfig;
import com.sample.agenttools.services.MessageService;
import com.sample.agenttools.tools.DateTimeTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.ApplicationContext;
import org.springframework.ai.tool.annotation.Tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatService {
    private final OpenAiChatModel openAiChatModel;
    private final MessageService messageService;
    private final OpenAiConfig openAiConfig;
    private final ApplicationContext applicationContext;

    @Autowired
    public ChatService(OpenAiChatModel openAiChatModel,
                       MessageService messageService,
                       OpenAiConfig openAiConfig,
                       ApplicationContext applicationContext) {
        this.openAiChatModel = openAiChatModel;
        this.messageService = messageService;
        this.openAiConfig = openAiConfig;
        this.applicationContext = applicationContext;
        log.info("Created ChatService with model={}", openAiConfig.getChat().getModel());
        log.debug("Using maxTokens={} for OpenAI completion", openAiConfig.getChat().getOptions().getMaxTokens());
    }

    private List<org.springframework.ai.chat.messages.Message> toChatHistory(List<Message> history) {
        return history.stream()
                .map(m -> switch (m.getRole()) {
                    case "user" -> new UserMessage(m.getContent());
                    case "assistant" -> new AssistantMessage(m.getContent());
                    case "system" -> new org.springframework.ai.chat.messages.SystemMessage(m.getContent());
                    default -> new UserMessage(m.getContent());
                })
                .collect(Collectors.toList());
    }


    public String getChatCompletion(String conversationId, String userPrompt, List<Message> history, Boolean callTools) {
        log.info("Generating chat completion for conversationId={}, userPrompt='{}', callTools={}", conversationId, userPrompt, callTools);
        var systemMessage = new org.springframework.ai.chat.messages.SystemMessage(
                "You are a helpful AI assistant. " +
                        "Use the provided tools to answer user questions when appropriate. " +
                        "Since the questions can be related to date and time ranges, " +
                        "you must always ensure that you have the current date and time. "
        );

        List<org.springframework.ai.chat.messages.Message> chatHistory = toChatHistory(history);
        chatHistory.add(0, systemMessage);
        log.debug("Chat history size: {}", chatHistory.size());
        chatHistory.add(new UserMessage(userPrompt));
        int maxTokens = openAiConfig.getChat().getOptions().getMaxTokens();

        ToolCallback[] dateTimeTools = ToolCallbacks.from(new DateTimeTools());


        var options = ToolCallingChatOptions
                .builder()
                .toolCallbacks(dateTimeTools)
                .maxTokens(maxTokens)
                .build();

        var prompt = new Prompt(chatHistory, options);
        var response = openAiChatModel.call(prompt);
        String assistantResponse = response.getResult().getOutput().getText();

        log.info("Assistant response: {}", assistantResponse);
        return assistantResponse;
    }
}
