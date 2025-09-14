package com.sample.agenttools.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "message")
@Getter
@Setter
public class Message {
    @Id
    @Column(name = "messageid", nullable = false, unique = true)
    private String messageid;

    @Column(name = "conversationid", nullable = false)
    private String conversationid;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "dateCreated", nullable = false)
    private LocalDateTime dateCreated;

    public Message() {}

    public Message(String messageid, String conversationid, String role, String content, LocalDateTime dateCreated) {
        this.messageid = messageid;
        this.conversationid = conversationid;
        this.role = role;
        this.content = content;
        this.dateCreated = dateCreated;
    }
}
