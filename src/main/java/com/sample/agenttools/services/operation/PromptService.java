package com.sample.agenttools.services.operation;

import com.sample.agenttools.api.DataNotFoundException;
import com.sample.agenttools.api.model.operation.Prompt;
import com.sample.agenttools.api.model.operation.PromptForInsert;
import com.sample.agenttools.api.model.operation.PromptForUpdate;
import com.sample.agenttools.data.operation.PromptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Service
public class PromptService {
    private final PromptRepository promptRepository;

    @Autowired
    public PromptService(PromptRepository promptRepository) {
        this.promptRepository = promptRepository;
    }

    public List<Prompt> getAllPrompts() {
        return promptRepository.findAll();
    }

    public Optional<Prompt> getPromptById(String id) {
        return promptRepository.findById(id);
    }

    public Prompt addPrompt(PromptForInsert promptForInsert) {
        Assert.hasLength(promptForInsert.id(), "The prompt id must not be empty");
        String id = promptForInsert.id();
        if (promptRepository.existsById(id)) {
            throw new IllegalArgumentException("Prompt with id '" + id + "' already exists.");
        }
        Prompt prompt = Prompt.fromInsert(promptForInsert);
        return promptRepository.save(prompt);
    }

    public Prompt updatePrompt(String id, PromptForUpdate promptForUpdate) {
        return promptRepository.findById(id)
            .map(existing -> {
                existing.setDescription(promptForUpdate.description());
                existing.setContent(promptForUpdate.content());
                existing.setUpdatedDate(java.time.LocalDateTime.now());
                return promptRepository.save(existing);
            })
            .orElseThrow(() -> new DataNotFoundException("Prompt with id '" + id + "' not found."));
    }

    public void deletePrompt(String id) {
        if (!promptRepository.existsById(id)) {
            throw new DataNotFoundException("Prompt with id '" + id + "' not found.");
        }
        promptRepository.deleteById(id);
    }
}
