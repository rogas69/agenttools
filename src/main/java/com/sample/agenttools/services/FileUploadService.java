package com.sample.agenttools.services;

import com.sample.agenttools.config.FileStorageProperties;
import com.sample.agenttools.data.ingestion.model.FileRecord;
import com.sample.agenttools.data.ingestion.model.FileRecordStatus;
import com.sample.agenttools.data.ingestion.service.FileRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service for handling file uploads.
 */
@Service
public class FileUploadService {
    private final FileStorageProperties fileStorageProperties;
    private final FileRecordService fileRecordService;

    @Autowired
    public FileUploadService(FileStorageProperties fileStorageProperties,
                             FileRecordService fileRecordService) {
        this.fileStorageProperties = fileStorageProperties;
        this.fileRecordService = fileRecordService;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String uploadPath = fileStorageProperties.getUploads().path();
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File dest = new File(dir, file.getOriginalFilename());
        file.transferTo(dest);
        // Calculate SHA-256 checksum for the file
        String sha256 = calculateSha256(dest);
        // Save file record in DB
        FileRecord fileRecord = new FileRecord();
        fileRecord.setId(sha256);
        fileRecord.setFileName(file.getOriginalFilename());
        fileRecord.setStatus(FileRecordStatus.CREATED.toString());
        fileRecord.setCreatedDate(LocalDateTime.now());
        fileRecordService.saveFileRecord(fileRecord);
        return dest.getAbsolutePath();
    }

    private String calculateSha256(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
            byte[] hash = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("SHA-256 algorithm not available", e);
        }
    }
}
