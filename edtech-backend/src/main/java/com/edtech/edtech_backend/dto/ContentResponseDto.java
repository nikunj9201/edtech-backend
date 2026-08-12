package com.edtech.edtech_backend.dto;

import com.edtech.edtech_backend.model.ContentAccessType;
import lombok.Data;

@Data
public class ContentResponseDto<T> {
    private T content;
    private boolean isLocked;
    private ContentAccessType accessType;

    public ContentResponseDto(T content, boolean isLocked, ContentAccessType accessType) {
        this.content = content;
        this.isLocked = isLocked;
        this.accessType = accessType;
    }
}
