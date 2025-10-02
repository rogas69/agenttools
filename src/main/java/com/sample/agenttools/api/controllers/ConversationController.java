package com.sample.agenttools.api.controllers;

import com.sample.agenttools.api.model.operation.Conversation;
import com.sample.agenttools.api.model.operation.ConversationForUpdate;
import com.sample.agenttools.api.model.operation.ConversationForInsert;
import com.sample.agenttools.api.model.operation.MessageForInsert;
import com.sample.agenttools.services.operation.ConversationService;
import com.sample.agenttools.services.operation.MessageService;
import com.sample.agenttools.services.ChatService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Conversation API", description = "Endpoints for managing conversations")
@Slf4j
@RestController
@RequestMapping("/conversation")
public class ConversationController {
    private final ConversationService conversationService;
    private final MessageService messageService;
    private final ChatService chatService;

    @Autowired
    public ConversationController(ConversationService conversationService, MessageService messageService, ChatService chatService) {
        this.conversationService = conversationService;
        this.messageService = messageService;
        this.chatService = chatService;
    }

    @Operation(summary = "Get all conversations", description = "Returns a list of all conversations.")
    @GetMapping
    public List<Conversation> getConversations() {
        return conversationService.getAllConversations();
    }

    @Operation(summary = "Get conversation by ID", description = "Returns a conversation by its unique identifier.")
    @GetMapping("/{conversationid}")
    public ResponseEntity<Conversation> getConversationById(@PathVariable String conversationid) {
        return conversationService.getConversationById(conversationid)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Add a new conversation", description = "Creates a new conversation with the given topic.")
    @PostMapping
    public ResponseEntity<Conversation> addConversation(@RequestBody ConversationForInsert conversationForInsert) {
        String topic = (conversationForInsert != null && conversationForInsert.topic() != null) ? conversationForInsert.topic() : "Default Conversation";
        Conversation conversation = Conversation.create(topic);
        conversationService.addConversation(conversation);
        return new ResponseEntity<>(conversation, HttpStatus.CREATED);
    }

    @Operation(summary = "Update conversation title", description = "Updates the title of an existing conversation.")
    @PutMapping("/{conversationid}")
    public ResponseEntity<Conversation> updateConversation(@PathVariable String conversationid, @RequestBody ConversationForUpdate update) {
        return conversationService.getConversationById(conversationid)
                .map(existing -> {
                    Conversation updated = new Conversation(conversationid, update.title(), existing.getDateCreated());
                    conversationService.updateConversation(updated);
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get messages for a conversation", description = "Returns all messages for a given conversation ID.")
    @GetMapping("/{conversationid}/messages")
    public ResponseEntity<List<com.sample.agenttools.api.model.operation.Message>> getMessagesForConversation(@PathVariable String conversationid) {
        var messages = messageService.getMessagesByConversationId(conversationid);
        return ResponseEntity.ok(messages);
    }

    @Operation(summary = "Add a user message to a conversation", description = "Inserts a new user message for the given conversation ID and returns the assistant's response.")
    @PostMapping("/{conversationid}/messages")
    public ResponseEntity<String> addUserMessageToConversation(
            @PathVariable String conversationid,
            @RequestBody MessageForInsert messageForInsert,
            @RequestParam(name = "call-tools", required = false, defaultValue = "true") Boolean callTools) {
        var history = messageService.getMessagesByConversationId(conversationid);
        messageService.addUserMessage(conversationid, messageForInsert);

        String assistantResponse = chatService.getChatCompletion(conversationid, messageForInsert.content(), history, callTools);
        messageService.addAssistantMessage(conversationid, new MessageForInsert(assistantResponse));

        return ResponseEntity.status(HttpStatus.CREATED).body(assistantResponse);
    }

    @Operation(summary = "Add a user message to a conversation with a graph", description = "Inserts a new user message for the given conversation ID and returns the assistant's response.")
    @PostMapping("/{conversationid}/chat/messages")
    public ResponseEntity<String> chatUsingGraph(
            @PathVariable String conversationid,
            @RequestBody MessageForInsert messageForInsert,
            @RequestParam(name = "call-tools", required = false, defaultValue = "true") Boolean callTools) {
        var history = messageService.getMessagesByConversationId(conversationid);
        messageService.addUserMessage(conversationid, messageForInsert);

        String assistantResponse = chatService.chatWithGraph(conversationid, messageForInsert.content(), history);
//        messageService.addAssistantMessage(conversationid, new MessageForInsert(assistantResponse));

        return ResponseEntity.status(HttpStatus.CREATED).body(assistantResponse);
    }

    @Operation(summary = "Clear all messages in a conversation", description = "Removes all messages for the given conversation ID. This resets the chat memory, allowing for easier testing.")
    @PutMapping("/{conversationid}/clear")
    public ResponseEntity<Void> clearConversationMessages(@PathVariable String conversationid) {
        log.warn("Clearing all messages for conversationId={}", conversationid);
        messageService.deleteMessagesByConversationId(conversationid);
        return ResponseEntity.noContent().build();
    }
}
