package com.sample.agenttools.api.controllers;

import com.sample.agenttools.config.FileStorageProperties;
import com.sample.agenttools.services.FileUploadService;
import com.sample.agenttools.data.ingestion.model.FileRecord;
import com.sample.agenttools.data.ingestion.service.FileRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/data")
@Slf4j
public class DataController {

    private final FileUploadService fileUploadService;
    private final FileRecordService fileRecordService;

    @Autowired
    public DataController(FileUploadService fileUploadService,
                          FileRecordService fileRecordService) {
        this.fileUploadService = fileUploadService;
        this.fileRecordService = fileRecordService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("Received file upload request: {}", file.getOriginalFilename());
        String savedPath = fileUploadService.uploadFile(file);
        log.info("File saved to: {}", savedPath);
        return ResponseEntity.ok("File uploaded successfully: " + savedPath);
    }

    @GetMapping("/files")
    public ResponseEntity<List<FileRecord>> getAllFileRecords() {
        List<FileRecord> records = fileRecordService.getAllFileRecords();
        return ResponseEntity.ok(records);
    }

    @DeleteMapping("/files/{id}")
    public ResponseEntity<Void> deleteFileRecord(@PathVariable String id) {
        fileRecordService.deleteFileRecordById(id);
        return ResponseEntity.noContent().build();
    }
}
