package com.edtech.edtech_backend.dto;

import com.edtech.edtech_backend.model.ContentAccessType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class VideoUploadDto {
    private String title;
    private Long courseId;
    // Client should upload the video file as multipart 'video'
    private MultipartFile video;
    // Optional internal stored filename/path (set by backend)
    private String storedFileName;
    private ContentAccessType accessType;
}
