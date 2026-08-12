package com.edtech.edtech_backend.service;

import com.edtech.edtech_backend.dto.ContentItemDTO;

import java.util.List;

public interface ContentService {
    List<ContentItemDTO> getContentByCategoryId(Long categoryId, String username);
}