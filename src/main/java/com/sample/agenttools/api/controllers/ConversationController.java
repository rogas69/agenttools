package com.sample.agenttools.api.controllers;

import com.sample.agenttools.api.model.Conversation;
import com.sample.agenttools.api.model.ConversationForUpdate;
import com.sample.agenttools.api.model.ConversationForInsert;
import com.sample.agenttools.services.ConversationService;
import com.sample.agenttools.services.MessageService;
import com.sample.agenttools.services.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Conversation API", description = "Endpoints for managing conversations")
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
    public ResponseEntity<List<com.sample.agenttools.api.model.Message>> getMessagesForConversation(@PathVariable String conversationid) {
        List<com.sample.agenttools.api.model.Message> messages = messageService.getMessagesByConversationId(conversationid);
        return ResponseEntity.ok(messages);
    }

    @Operation(summary = "Add a user message to a conversation", description = "Inserts a new user message for the given conversation ID.")
    @PostMapping("/{conversationid}/messages")
    public ResponseEntity<Void> addUserMessageToConversation(@PathVariable String conversationid, @RequestBody com.sample.agenttools.api.model.MessageForInsert messageForInsert) {
        messageService.addUserMessage(conversationid, messageForInsert);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
