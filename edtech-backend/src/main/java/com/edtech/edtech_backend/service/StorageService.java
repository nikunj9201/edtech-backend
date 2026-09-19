package com.edtech.edtech_backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    /**
     * Store file under optional subFolder and return stored object key/path
     */
    String storeFile(MultipartFile file, String subFolder);
}
