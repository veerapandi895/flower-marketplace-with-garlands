package com.flowermarket.controller;

import com.flowermarket.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/** Phase 2: real image upload for sellers - flowers, garlands, shop logo/banner. */
@RestController
@RequestMapping("/api/seller/uploads")
@RequiredArgsConstructor
public class SellerUploadController {

    private final FileStorageService fileStorageService;

    @PostMapping(value = "/flower-images", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, List<String>>> uploadFlowerImages(@RequestParam("files") List<MultipartFile> files) {
        List<String> urls = fileStorageService.storeImages(files, "flowers");
        return ResponseEntity.ok(Map.of("urls", urls));
    }

    @PostMapping(value = "/garland-images", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, List<String>>> uploadGarlandImages(@RequestParam("files") List<MultipartFile> files) {
        List<String> urls = fileStorageService.storeImages(files, "garlands");
        return ResponseEntity.ok(Map.of("urls", urls));
    }

    @PostMapping(value = "/shop-logo", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> uploadShopLogo(@RequestParam("file") MultipartFile file) {
        String url = fileStorageService.storeImage(file, "shop-logos");
        return ResponseEntity.ok(Map.of("url", url));
    }

    @PostMapping(value = "/shop-banner", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> uploadShopBanner(@RequestParam("file") MultipartFile file) {
        String url = fileStorageService.storeImage(file, "shop-banners");
        return ResponseEntity.ok(Map.of("url", url));
    }
}
