package com.flowermarket.service;

import com.flowermarket.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * Phase 2: Upload Your Gallery.
 * Stores uploaded images locally under the configured uploads directory and
 * returns web-accessible URLs (served via /uploads/** static mapping).
 */
@Service
@Slf4j
public class FileStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final long MAX_FILE_SIZE_BYTES = 10L * 1024 * 1024; // 10MB, matches application.properties

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.upload.base-url:/uploads}")
    private String uploadBaseUrl;

    /** Stores one image sub-folder (e.g. "flowers", "garlands", "shops") and returns its public URL. */
    public String storeImage(MultipartFile file, String subFolder) {
        validate(file);

        String extension = extensionOf(file.getOriginalFilename());
        String storedName = UUID.randomUUID() + "." + extension;

        try {
            Path targetDir = Paths.get(uploadDir, subFolder).toAbsolutePath().normalize();
            Files.createDirectories(targetDir);

            Path targetFile = targetDir.resolve(storedName).normalize();
            if (!targetFile.getParent().equals(targetDir)) {
                throw new BadRequestException("Invalid file name");
            }

            Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Failed to store uploaded file", e);
            throw new BadRequestException("Could not store uploaded file: " + e.getMessage());
        }

        return uploadBaseUrl + "/" + subFolder + "/" + storedName;
    }

    public List<String> storeImages(List<MultipartFile> files, String subFolder) {
        return files.stream().map(f -> storeImage(f, subFolder)).toList();
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Uploaded file is empty");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new BadRequestException("File is too large (max 10MB)");
        }
        String extension = extensionOf(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestException("Unsupported image type. Allowed: jpg, jpeg, png, webp");
        }
    }

    private String extensionOf(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new BadRequestException("File has no extension");
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }
}
