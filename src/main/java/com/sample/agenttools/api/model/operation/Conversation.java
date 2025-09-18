package com.sample.agenttools.api.model.operation;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "conversation")
public class Conversation {
    @Id
    private String id;

    @Column(nullable = false)
    private String topic;

    @Column(name = "date_created", nullable = false)
    private String dateCreated;

    public Conversation() {}

    public Conversation(String id, String topic, String dateCreated) {
        this.id = id;
        this.topic = topic;
        this.dateCreated = dateCreated;
    }

    public static Conversation create(String topic) {
        return new Conversation(java.util.UUID.randomUUID().toString(), topic, java.time.LocalDateTime.now().toString());
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getDateCreated() { return dateCreated; }
    public void setDateCreated(String dateCreated) { this.dateCreated = dateCreated; }
}
