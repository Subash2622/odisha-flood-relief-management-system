package com.odisha.floodrelief.util;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import com.odisha.floodrelief.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Component
public class FileStorageUtil {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public Path getUploadRoot() {
        return Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    public String storeFile(MultipartFile file, String subDir) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            String originalName = file.getOriginalFilename();
            String extension = originalName != null && originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf(".")) : "";
            String fileName = UUID.randomUUID().toString() + extension;

            Path dirPath = getUploadRoot().resolve(subDir);
            Files.createDirectories(dirPath);
            Path target = dirPath.resolve(fileName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + subDir + "/" + fileName;
        } catch (IOException e) {
            log.error("Failed to store file", e);
            throw new BadRequestException("Failed to store file: " + e.getMessage());
        }
    }
}
