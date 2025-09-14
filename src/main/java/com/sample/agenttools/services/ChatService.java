package com.sample.agenttools.services;

import com.sample.agenttools.api.model.Message;
import com.sample.agenttools.config.OpenAiConfig;
import com.sample.agenttools.services.MessageService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private final ChatClient chatClient;
    private final MessageService messageService;
    private final OpenAiConfig openAiConfig;

    @Autowired
    public ChatService(ChatClient chatClient, MessageService messageService, OpenAiConfig openAiConfig) {
        this.chatClient = chatClient;
        this.messageService = messageService;
        this.openAiConfig = openAiConfig;
    }

    private List<org.springframework.ai.chat.messages.Message> getChatHistory(String conversationId) {
        List<Message> history = messageService.getMessagesByConversationId(conversationId);
        return history.stream()
            .map(m -> switch (m.getRole()) {
                case "user" -> new UserMessage(m.getContent());
                case "assistant" -> new AssistantMessage(m.getContent());
                case "system" -> new org.springframework.ai.chat.messages.SystemMessage(m.getContent());
                default -> new UserMessage(m.getContent());
            })
            .collect(Collectors.toList());
    }

    public String getChatCompletion(String conversationId, String userPrompt) {
        // Retrieve conversation history
        List<org.springframework.ai.chat.messages.Message> chatHistory = getChatHistory(conversationId);
        // Add the new user message
        chatHistory.add(new UserMessage(userPrompt));
        // Build the Prompt object with max tokens option from config
        int maxTokens = openAiConfig.getChat().getOptions().getMaxTokens();
        OpenAiChatOptions options = OpenAiChatOptions
                .builder()
                .maxCompletionTokens(maxTokens)
                .build();
        Prompt prompt = new Prompt(chatHistory, options);
        // Call the LLM with the Prompt
        var assistantResponse = chatClient.prompt(prompt);
        var content = assistantResponse.call().content();
        return content;
    }
}
