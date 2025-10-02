package com.sample.agenttools.services;

import com.sample.agenttools.api.model.operation.Message;
import com.sample.agenttools.config.OpenAiConfig;
import com.sample.agenttools.langgraph.ChatMemoryState;
import com.sample.agenttools.langgraph.services.SimpleChatGraph;
import com.sample.agenttools.services.operation.MessageService;
import com.sample.agenttools.tools.DateTimeTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.ApplicationContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatService {
    private final OpenAiChatModel openAiChatModel;
    private final ChatClient.Builder chatClientBuilder;
    private final MessageService messageService;
    private final OpenAiConfig openAiConfig;
    private final ApplicationContext applicationContext;
    private final SimpleChatGraph simpleChatGraph;

    @Autowired
    public ChatService(OpenAiChatModel openAiChatModel,
                       ChatClient.Builder chatClientBuilder,
                       MessageService messageService,
                       OpenAiConfig openAiConfig,
                       ApplicationContext applicationContext,
                       SimpleChatGraph simpleChatGraph) {
        this.simpleChatGraph = simpleChatGraph;
        this.openAiChatModel = openAiChatModel;
        this.chatClientBuilder = chatClientBuilder;
        this.messageService = messageService;
        this.openAiConfig = openAiConfig;
        this.applicationContext = applicationContext;
        log.info("Created ChatService with model={}", openAiConfig.getChat()
                .getModel());
        log.debug("Using maxTokens={} for OpenAI completion", openAiConfig.getChat()
                .getOptions()
                .getMaxTokens());
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


    public String chatWithGraph(String conversationId, String userPrompt, List<Message> history) {
        log.info("Starting chat with graph for conversationId={}, userPrompt='{}'", conversationId, userPrompt);
        var initialInputs = history.stream()
                .map(Message::getContent)
                .collect(Collectors.toList());
        initialInputs.add(userPrompt);

        log.debug("Initial inputs for graph: {}", initialInputs);

        var inputs =  java.util.Map.of(ChatMemoryState.MESSAGES_KEY, (Object) initialInputs);

        for (var item : simpleChatGraph.stream(inputs)) {
            log.info( "Output item: {}", item );
        }
        return "";
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
        int maxTokens = openAiConfig.getChat()
                .getOptions()
                .getMaxTokens();

        ToolCallback[] dateTimeTools = ToolCallbacks.from(new DateTimeTools());

        var options = ToolCallingChatOptions
                .builder()
                .toolCallbacks(dateTimeTools)
                .maxTokens(maxTokens)
                .build();

        var prompt = new Prompt(chatHistory, options);

        var chatClient = chatClientBuilder
                .build();
        var response = chatClient.prompt(prompt)
                .call();
        String assistantResponse = response.content();


        log.info("Assistant response: {}", assistantResponse);
        return assistantResponse;
    }
}
