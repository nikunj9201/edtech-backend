package com.edtech.edtech_backend.controller;

import com.edtech.edtech_backend.dto.ContentItemDTO;
import com.edtech.edtech_backend.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/content")
public class ContentController {

    @Autowired
    private ContentService contentService;

    @GetMapping("/category/{categoryId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<ContentItemDTO>> getContent(@PathVariable Long categoryId, Principal principal) {
        String username = principal.getName();
        List<ContentItemDTO> content = contentService.getContentByCategoryId(categoryId, username);
        return ResponseEntity.ok(content);
    }
}
