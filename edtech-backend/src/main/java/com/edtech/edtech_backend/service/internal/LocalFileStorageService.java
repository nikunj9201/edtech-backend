package com.edtech.edtech_backend.service.internal;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class LocalFileStorageService implements com.edtech.edtech_backend.service.StorageService {

    private final Path baseUploadDir;

    public LocalFileStorageService() {
        // Create an "uploads" folder in the application's working directory (backend module when running from IDE)
        this.baseUploadDir = Paths.get("uploads").toAbsolutePath().normalize();
        try {
            Files.createDirectories(baseUploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create base upload directory", e);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String subFolder) {
        try {
            Path targetDir = (subFolder == null || subFolder.isBlank()) ? baseUploadDir : baseUploadDir.resolve(subFolder);
            Files.createDirectories(targetDir);

            String original = file.getOriginalFilename();
            String safeName = System.currentTimeMillis() + "_" + (original != null ? original.replaceAll("[^a-zA-Z0-9.\\-_]", "_") : "file");
            Path target = targetDir.resolve(safeName);
            file.transferTo(target.toFile());
            // Return relative stored filename including subfolder
            return (subFolder == null || subFolder.isBlank()) ? safeName : (subFolder + "/" + safeName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}
