package com.sample.agenttools.services;

import com.sample.agenttools.api.model.Message;
import com.sample.agenttools.config.OpenAiConfig;
import com.sample.agenttools.services.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;

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

    @Autowired
    public ChatService(OpenAiChatModel openAiChatModel,
                       MessageService messageService,
                       OpenAiConfig openAiConfig) {
        this.openAiChatModel = openAiChatModel;
        this.messageService = messageService;
        this.openAiConfig = openAiConfig;
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

    public String getChatCompletion(String conversationId, String userPrompt, List<Message> history) {
        log.info("Generating chat completion for conversationId={}, userPrompt='{}'", conversationId, userPrompt);
        List<org.springframework.ai.chat.messages.Message> chatHistory = toChatHistory(history);
        log.debug("Chat history size: {}", chatHistory.size());
        chatHistory.add(new UserMessage(userPrompt));
        int maxTokens = openAiConfig.getChat().getOptions().getMaxTokens();

        OpenAiChatOptions options = OpenAiChatOptions.builder().maxTokens(maxTokens).build();
        Prompt prompt = new Prompt(chatHistory, options);
        var response = openAiChatModel.call(prompt);
        String assistantResponse = response.getResult().getOutput().getText();
        log.info("Assistant response: {}", assistantResponse);
        return assistantResponse;
    }
}
