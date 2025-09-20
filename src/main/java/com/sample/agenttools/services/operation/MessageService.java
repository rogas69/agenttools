package com.sample.agenttools.services.operation;

import com.sample.agenttools.api.model.operation.Message;
import com.sample.agenttools.api.model.operation.MessageForInsert;
import com.sample.agenttools.data.operation.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Optional<Message> getMessageById(String messageid) {
        return messageRepository.findById(messageid);
    }

    public void addMessage(Message message) {
        messageRepository.save(message);
    }

    public void updateMessage(Message message) {
        messageRepository.save(message);
    }

    public void deleteMessage(String messageid) {
        messageRepository.deleteById(messageid);
    }

    public List<Message> getMessagesByConversationId(String conversationid) {
        return messageRepository.findByConversationid(conversationid)
                .stream()
                .sorted(java.util.Comparator.comparing(Message::getDateCreated))
                .toList();
    }

    private void addRoleMessage(String conversationid, MessageForInsert messageForInsert, String role) {
        Message message = new Message();
        message.setMessageid(UUID.randomUUID().toString());
        message.setConversationid(conversationid);
        message.setRole(role);
        message.setContent(messageForInsert.content());
        message.setDateCreated(LocalDateTime.now());
        messageRepository.save(message);
    }

    public void addUserMessage(String conversationid, MessageForInsert messageForInsert) {
        addRoleMessage(conversationid, messageForInsert, "user");
    }

    public void addAssistantMessage(String conversationid, MessageForInsert messageForInsert) {
        addRoleMessage(conversationid, messageForInsert, "assistant");
    }

    public void addSystemMessage(String conversationid, MessageForInsert messageForInsert) {
        addRoleMessage(conversationid, messageForInsert, "system");
    }

    public void deleteMessagesByConversationId(String conversationid) {
        var messages = getMessagesByConversationId(conversationid);
        for (Message message : messages) {
            messageRepository.deleteById(message.getMessageid());
        }
    }
}
