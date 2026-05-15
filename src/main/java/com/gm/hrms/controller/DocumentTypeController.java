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
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/document-types")
@RequiredArgsConstructor
public class DocumentTypeController {

    private final DocumentTypeService service;

    // ── CREATE → ADMIN only ───────────────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DocumentTypeResponseDTO>> create(
            @Valid @RequestBody DocumentTypeRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.<DocumentTypeResponseDTO>builder()
                        .success(true)
                        .message("Document type created successfully")
                        .data(service.create(dto))
                        .build()
        );
    }

    // ── UPDATE / PATCH → ADMIN only ───────────────────────────────────────────
    // Accepts { name, key, applicableTypes, mandatory, active }
    // 'active' field is used to toggle status (activate / deactivate)
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DocumentTypeResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody DocumentTypeRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.<DocumentTypeResponseDTO>builder()
                        .success(true)
                        .message("Document type updated successfully")
                        .data(service.update(id, dto))
                        .build()
        );
    }

    // ── DELETE (soft) → ADMIN only ────────────────────────────────────────────
    // Sets active = false. Record stays in DB and still appears in listing
    // with "Inactive" status badge. Does NOT physically remove the row.
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Document type deactivated successfully")
                        .build()
        );
    }

    // ── GET ALL (active + inactive) → ADMIN + HR ──────────────────────────────
    // FIX: was also allowing EMPLOYEE — documents config is internal admin data
    // Returns all records regardless of active status, sorted by id desc
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<PageResponseDTO<DocumentTypeResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<DocumentTypeResponseDTO>>builder()
                        .success(true)
                        .message("Document types fetched successfully")
                        .data(service.getAll(
                                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))
                        ))
                        .build()
        );
    }

    // ── GET BY ID → ADMIN + HR ────────────────────────────────────────────────
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<DocumentTypeResponseDTO>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.<DocumentTypeResponseDTO>builder()
                        .success(true)
                        .message("Document type fetched successfully")
                        .data(service.getById(id))
                        .build()
        );
    }

    // ── FILTER BY APPLICABLE TYPE → ADMIN + HR ────────────────────────────────
    // GET /api/document-types/type/EMPLOYEE?page=0&size=10
    // Returns ALL (active + inactive) filtered by applicable type
    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<PageResponseDTO<DocumentTypeResponseDTO>>> getByApplicableType(
            @PathVariable ApplicableType type,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<DocumentTypeResponseDTO>>builder()
                        .success(true)
                        .message("Document types fetched successfully")
                        .data(service.getByApplicableType(
                                type,
                                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))
                        ))
                        .build()
        );
    }

    // ── STATS → ADMIN + HR ────────────────────────────────────────────────────
    // GET /api/document-types/stats
    // Returns { total, active, inactive } counts
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<DocumentTypeStatsDTO>> getStats() {

        return ResponseEntity.ok(
                ApiResponse.<DocumentTypeStatsDTO>builder()
                        .success(true)
                        .message("Stats fetched successfully")
                        .data(service.getStats())
                        .build()
        );
    }
}