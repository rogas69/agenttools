package com.sample.agenttools.data.operation;

import com.sample.agenttools.api.model.operation.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromptRepository extends JpaRepository<Prompt, String> {
}

