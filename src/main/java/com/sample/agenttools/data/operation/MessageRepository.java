package com.sample.agenttools.data.operation;

import com.sample.agenttools.api.model.operation.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {
    java.util.List<Message> findByConversationid(String conversationid);
}
