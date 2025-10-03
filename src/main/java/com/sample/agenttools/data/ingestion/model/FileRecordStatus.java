package com.sample.agenttools.data.ingestion.model;

public enum FileRecordStatus {
    CREATED,
    EMBEDDING,
    EMBEDDED,
    FAILED;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}

