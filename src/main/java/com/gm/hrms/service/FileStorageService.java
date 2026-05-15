package com.gm.hrms.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


@Service
public class FileStorageService {

    @Value("${app.file-upload.base-dir:uploads/}")
    private String baseDir;

    private static final DateTimeFormatter MONTH_FMT =
            DateTimeFormatter.ofPattern("yyyy/MM");

    // ─── Public API ───────────────────────────────────────────────────────────

    /**
     * Save a profile image under  uploads/profile-images/YYYY/MM/
     */
    public String saveProfileImage(MultipartFile file) {
        return store(file, "profile-images");
    }

    /**
     * Save a document under  uploads/documents/{subCategory}/YYYY/MM/
     * subCategory examples: "employee", "intern", "trainee", "general"
     */
    public String saveDocument(MultipartFile file, String subCategory) {
        String category = "documents/" + sanitise(subCategory);
        return store(file, category);
    }

    /**
     * Generic save — falls back to uploads/misc/YYYY/MM/
     * Kept for backward-compatibility with existing call sites.
     */
    public String save(MultipartFile file) {
        return store(file, "misc");
    }

    // ─── Core storage logic ───────────────────────────────────────────────────

    private String store(MultipartFile file, String category) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File must not be empty");
        }

        // Build   uploads/{category}/YYYY/MM/
        String monthPath = LocalDate.now().format(MONTH_FMT);
        Path targetDir   = Paths.get(baseDir, category, monthPath);

        try {
            Files.createDirectories(targetDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + targetDir, e);
        }

        String originalName = sanitiseFilename(file.getOriginalFilename());
        String storedName   = UUID.randomUUID().toString().replace("-", "") + "_" + originalName;
        Path   destination  = targetDir.resolve(storedName);

        try {
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + originalName, e);
        }

        // Return a normalized, forward-slash path suitable for DB storage
        return destination.toString().replace("\\", "/");
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    /** Strip path-traversal characters from the original filename. */
    private String sanitiseFilename(String name) {
        if (name == null || name.isBlank()) return "file";
        // Keep only the last segment (strip any directory prefix the browser might send)
        String base = Paths.get(name).getFileName().toString();
        // Replace whitespace and dangerous chars with underscore
        return base.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    /** Prevent path-traversal in category names. */
    private String sanitise(String category) {
        if (category == null || category.isBlank()) return "general";
        return category.replaceAll("[^a-zA-Z0-9_-]", "_").toLowerCase();
    }
}