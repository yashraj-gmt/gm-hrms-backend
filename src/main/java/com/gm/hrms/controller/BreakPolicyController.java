package com.gm.hrms.controller;

import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.audit.Auditable;
import com.gm.hrms.dto.request.BreakPolicyRequestDTO;
import com.gm.hrms.dto.response.BreakPolicyResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.enums.BreakCategory;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.BreakPolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/break-policies")
@RequiredArgsConstructor
public class BreakPolicyController {

    private final BreakPolicyService service;

    // ── CREATE  →  ADMIN + HR ─────────────────────────────────────────────────
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping
    @Auditable(action = AuditAction.CREATE_BREAK_POLICY, resource = "BreakPolicy", description = "Create break policy")
    public ResponseEntity<ApiResponse<BreakPolicyResponseDTO>> create(
            @Valid @RequestBody BreakPolicyRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.<BreakPolicyResponseDTO>builder()
                        .success(true)
                        .message("Break policy created successfully")
                        .data(service.create(dto))
                        .build()
        );
    }

    // ── UPDATE  →  ADMIN + HR  (also handles toggle via isActive field) ───────
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}")
    @Auditable(action = AuditAction.UPDATE_BREAK_POLICY, resource = "BreakPolicy", description = "Update break policy")
    public ResponseEntity<ApiResponse<BreakPolicyResponseDTO>> update(
            @PathVariable Long id,
            @RequestBody BreakPolicyRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.<BreakPolicyResponseDTO>builder()
                        .success(true)
                        .message("Break policy updated successfully")
                        .data(service.update(id, dto))
                        .build()
        );
    }

    // ── GET BY ID  →  ADMIN + HR ──────────────────────────────────────────────
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BreakPolicyResponseDTO>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.<BreakPolicyResponseDTO>builder()
                        .success(true)
                        .message("Break policy fetched successfully")
                        .data(service.getById(id))
                        .build()
        );
    }

    // ── GET ALL  →  ADMIN + HR
    //    Supports: ?search=tea&category=FIXED&isPaid=true&page=0&size=10
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<BreakPolicyResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0")  int          page,
            @RequestParam(defaultValue = "10") int          size,
            @RequestParam(required = false)    String       search,
            @RequestParam(required = false)    BreakCategory category,
            @RequestParam(required = false)    Boolean      isPaid) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("breakName").ascending());

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<BreakPolicyResponseDTO>>builder()
                        .success(true)
                        .message("Break policies fetched successfully")
                        .data(service.getAll(search, category, isPaid, pageable))
                        .build()
        );
    }

    // ── DELETE (soft)  →  ADMIN ONLY ─────────────────────────────────────────
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Auditable(action = AuditAction.DELETE_BREAK_POLICY, resource = "BreakPolicy", description = "Deactivate break policy")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Break policy deactivated successfully")
                        .build()
        );
    }
}