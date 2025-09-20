package com.sample.agenttools.api.model.operation;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.time.LocalDateTime;

@Entity
@Table(name = "prompt")
public class Prompt {
    @Id
    private String id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "createdDate", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "updatedDate", nullable = false)
    private LocalDateTime updatedDate;

    public Prompt() {}

    public Prompt(String id, String description, String content, LocalDateTime createdDate, LocalDateTime updatedDate) {
        this.id = id;
        this.description = description;
        this.content = content;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    public static Prompt create(String description, String content) {
        LocalDateTime now = LocalDateTime.now();
        return new Prompt(java.util.UUID.randomUUID().toString(), description, content, now, now);
    }

    public static Prompt fromInsert(PromptForInsert insert) {
        LocalDateTime now = LocalDateTime.now();
        return new Prompt(
            insert.id(),
            insert.description(),
            insert.content(),
            now,
            now
        );
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }
}
