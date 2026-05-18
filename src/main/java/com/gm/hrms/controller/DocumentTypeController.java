package com.gm.hrms.controller;

import com.gm.hrms.dto.request.DocumentTypeRequestDTO;
import com.gm.hrms.dto.response.DocumentTypeResponseDTO;
import com.gm.hrms.dto.response.DocumentTypeStatsDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.enums.ApplicableType;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.DocumentTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/document-types")
@RequiredArgsConstructor
public class DocumentTypeController {

    private final DocumentTypeService service;

    // ── CREATE ────────────────────────────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DocumentTypeResponseDTO>> create(
            @Valid @RequestBody DocumentTypeRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.<DocumentTypeResponseDTO>builder()
                .success(true).message("Document type created successfully")
                .data(service.create(dto)).build());
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DocumentTypeResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody DocumentTypeRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.<DocumentTypeResponseDTO>builder()
                .success(true).message("Document type updated successfully")
                .data(service.update(id, dto)).build());
    }

    // ── DELETE (soft) ─────────────────────────────────────────────────────────
    // Sets deleted=true. Record is hidden from all listings, retained in DB.
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Document type deleted successfully").build());
    }

    // ── GET ALL ───────────────────────────────────────────────────────────────
    // Returns non-deleted docs (active + inactive).
    // Optional ?applicableTypes=EMPLOYEE&applicableTypes=INTERN for multi-filter.
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<PageResponseDTO<DocumentTypeResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false)    Set<ApplicableType> applicableTypes) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        PageResponseDTO<DocumentTypeResponseDTO> data =
                (applicableTypes != null && !applicableTypes.isEmpty())
                        ? service.getByApplicableTypes(applicableTypes, pageable)
                        : service.getAll(pageable);

        return ResponseEntity.ok(ApiResponse.<PageResponseDTO<DocumentTypeResponseDTO>>builder()
                .success(true).message("Document types fetched successfully")
                .data(data).build());
    }

    // ── GET BY ID ─────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<DocumentTypeResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<DocumentTypeResponseDTO>builder()
                .success(true).message("Document type fetched successfully")
                .data(service.getById(id)).build());
    }

    // ── FILTER BY SINGLE TYPE (kept for backward compatibility) ───────────────
    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<PageResponseDTO<DocumentTypeResponseDTO>>> getByApplicableType(
            @PathVariable ApplicableType type,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.<PageResponseDTO<DocumentTypeResponseDTO>>builder()
                .success(true).message("Document types fetched successfully")
                .data(service.getByApplicableType(
                        type, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))))
                .build());
    }

    // ── STATS ─────────────────────────────────────────────────────────────────
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<DocumentTypeStatsDTO>> getStats() {
        return ResponseEntity.ok(ApiResponse.<DocumentTypeStatsDTO>builder()
                .success(true).message("Stats fetched successfully")
                .data(service.getStats()).build());
    }
}