package com.edtech.edtech_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentItemDTO {
    private Long id;
    private String title;
    private String type;
    private boolean isPremium;
    private Long categoryId;
    // Client will upload the file as multipart; do not pass URLs from client.
    private MultipartFile file;
    // Optional internal stored filename/path (set by backend) - not required from client
    private String storedFileName;

    // Backward-compatible constructor for older service code that used fileUrl as a String.
    public ContentItemDTO(Long id, String title, String type, boolean isPremium, Long categoryId, String fileUrl) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.isPremium = isPremium;
        this.categoryId = categoryId;
        this.file = null;
        this.storedFileName = fileUrl;
    }
}