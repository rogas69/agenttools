package com.sample.agenttools.data.ingestion.service;

import com.sample.agenttools.data.ingestion.model.FileRecord;
import com.sample.agenttools.data.ingestion.repository.FileRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileRecordService {
    private final FileRecordRepository fileRecordRepository;

    @Autowired
    public FileRecordService(FileRecordRepository fileRecordRepository) {
        this.fileRecordRepository = fileRecordRepository;
    }

    public FileRecord saveFileRecord(FileRecord fileRecord) {
        return fileRecordRepository.save(fileRecord);
    }

    public List<FileRecord> getAllFileRecords() {
        return fileRecordRepository.findAll();
    }

    public void deleteFileRecordById(String id) {
        fileRecordRepository.deleteById(id);
    }
}
