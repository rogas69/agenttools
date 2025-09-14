package com.sample.agenttools.data;

import com.sample.agenttools.api.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {
    java.util.List<Message> findByConversationid(String conversationid);
}
