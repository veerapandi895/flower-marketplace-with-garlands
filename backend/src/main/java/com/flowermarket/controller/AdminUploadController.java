package com.flowermarket.controller;

import com.flowermarket.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/** Phase 2: real image upload for admin - category images. */
@RestController
@RequestMapping("/api/admin/uploads")
@RequiredArgsConstructor
public class AdminUploadController {

    private final FileStorageService fileStorageService;

    @PostMapping(value = "/category-image", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> uploadCategoryImage(@RequestParam("file") MultipartFile file) {
        String url = fileStorageService.storeImage(file, "categories");
        return ResponseEntity.ok(Map.of("url", url));
    }
}
