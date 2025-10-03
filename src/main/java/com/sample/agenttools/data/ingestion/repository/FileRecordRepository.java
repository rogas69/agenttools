package com.sample.agenttools.data.ingestion.repository;

import com.sample.agenttools.data.ingestion.model.FileRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRecordRepository extends JpaRepository<FileRecord, String> {
    // Additional query methods can be defined here if needed
}

