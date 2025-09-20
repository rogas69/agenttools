package com.sample.agenttools.services.operation;

import com.sample.agenttools.api.model.operation.Conversation;
import com.sample.agenttools.data.operation.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConversationService {
    private final ConversationRepository conversationRepository;

    @Autowired
    public ConversationService(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    public List<Conversation> getAllConversations() {
        return conversationRepository.findAll();
    }

    public Optional<Conversation> getConversationById(String id) {
        return conversationRepository.findById(id);
    }

    public void addConversation(Conversation conversation) {
        conversationRepository.save(conversation);
    }

    public void updateConversation(Conversation conversation) {
        conversationRepository.save(conversation);
    }
}
