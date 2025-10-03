package com.sample.agenttools.api.controllers;

import com.sample.agenttools.config.FileStorageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/data")
@Slf4j
public class DataController {

    private final FileStorageProperties fileStorageProperties;

    @Autowired
    public DataController(FileStorageProperties fileStorageProperties) {
        this.fileStorageProperties = fileStorageProperties;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("Received file upload request: {}", file.getOriginalFilename());
        String uploadPath = fileStorageProperties.getUploads().path();
        try {
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File dest = new File(dir, file.getOriginalFilename());
            file.transferTo(dest);
            log.info("File saved to: {}", dest.getAbsolutePath());
            return ResponseEntity.ok("File uploaded successfully: " + dest.getAbsolutePath());
        } catch (IOException e) {
            log.error("File upload failed: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }
    }
}
