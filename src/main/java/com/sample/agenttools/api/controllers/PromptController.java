package com.sample.agenttools.api.controllers;

import com.sample.agenttools.api.model.operation.Prompt;
import com.sample.agenttools.api.model.operation.PromptForInsert;
import com.sample.agenttools.api.model.operation.PromptForUpdate;
import com.sample.agenttools.services.operation.PromptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Prompt API", description = "Endpoints for managing prompts")
@Slf4j
@RestController
@RequestMapping("/prompts")
public class PromptController {
    private final PromptService promptService;

    @Autowired
    public PromptController(PromptService promptService) {
        this.promptService = promptService;
    }

    @Operation(summary = "Get all prompts", description = "Returns a list of all prompts.")
    @GetMapping
    public ResponseEntity<List<Prompt>> getAllPrompts() {
        log.info("Fetching all prompts");
        List<Prompt> prompts = promptService.getAllPrompts();
        return ResponseEntity.ok(prompts);
    }

    @Operation(summary = "Get prompt by ID", description = "Returns a prompt by its unique identifier.")
    @GetMapping("/{id}")
    public ResponseEntity<Prompt> getPromptById(@PathVariable String id) {
        log.info("Fetching prompt with id={}", id);
        Optional<Prompt> prompt = promptService.getPromptById(id);
        return prompt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new prompt", description = "Creates a new prompt.")
    @PostMapping
    public ResponseEntity<Prompt> createPrompt(@RequestBody PromptForInsert promptForInsert) {
        log.info("Creating new prompt {} with description={}",
                promptForInsert.id(), promptForInsert.description());
        Prompt created = promptService.addPrompt(promptForInsert);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @Operation(summary = "Update a prompt", description = "Updates an existing prompt.")
    @PutMapping("/{id}")
    public ResponseEntity<Prompt> updatePrompt(@PathVariable String id, @RequestBody PromptForUpdate promptForUpdate) {
        log.info("Updating prompt with id={}", id);
            Prompt updated = promptService.updatePrompt(id, promptForUpdate);
            return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete a prompt", description = "Deletes a prompt by its unique identifier.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrompt(@PathVariable String id) {
        log.info("Deleting prompt with id={}", id);
        promptService.deletePrompt(id);
        return ResponseEntity.noContent().build();
    }
}
