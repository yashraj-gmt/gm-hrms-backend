package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.dto.request.LeavePolicyRequestDTO;
import com.gm.hrms.dto.response.LeavePolicyResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.LeavePolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave-policies")
@RequiredArgsConstructor
public class LeavePolicyController {

    private final LeavePolicyService service;

    // ================= CREATE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping
    @Auditable(
            action      = AuditAction.CREATE_LEAVE_POLICY,
            resource    = "LeavePolicy",
            description = "Create leave policy"
    )
    public ResponseEntity<ApiResponse<LeavePolicyResponseDTO>> create(
            @Valid @RequestBody LeavePolicyRequestDTO request) {

        return ResponseEntity.ok(
                ApiResponse.<LeavePolicyResponseDTO>builder()
                        .success(true)
                        .message("Policy created successfully")
                        .data(service.create(request))
                        .build()
        );
    }

    // ================= GET BY ID =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeavePolicyResponseDTO>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.<LeavePolicyResponseDTO>builder()
                        .success(true)
                        .message("Policy fetched successfully")
                        .data(service.getById(id))
                        .build()
        );
    }

    // ================= GET ALL =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<LeavePolicyResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<LeavePolicyResponseDTO>>builder()
                        .success(true)
                        .message("Policies fetched successfully")
                        .data(service.getAll(PageRequest.of(page, size)))
                        .build()
        );
    }

    // ================= PUT =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PutMapping("/{id}")
    @Auditable(
            action      = AuditAction.UPDATE_LEAVE_POLICY,
            resource    = "LeavePolicy",
            description = "Full update of leave policy"
    )
    public ResponseEntity<ApiResponse<LeavePolicyResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody LeavePolicyRequestDTO request) {

        return ResponseEntity.ok(
                ApiResponse.<LeavePolicyResponseDTO>builder()
                        .success(true)
                        .message("Policy updated successfully")
                        .data(service.update(id, request))
                        .build()
        );
    }

    // ================= PATCH =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}")
    @Auditable(
            action      = AuditAction.UPDATE_LEAVE_POLICY,
            resource    = "LeavePolicy",
            description = "Partial update of leave policy"
    )
    public ResponseEntity<ApiResponse<LeavePolicyResponseDTO>> patch(
            @PathVariable Long id,
            @RequestBody LeavePolicyRequestDTO request) {

        return ResponseEntity.ok(
                ApiResponse.<LeavePolicyResponseDTO>builder()
                        .success(true)
                        .message("Policy updated successfully")
                        .data(service.patchUpdate(id, request))
                        .build()
        );
    }

    // ================= DELETE =================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Auditable(
            action      = AuditAction.DELETE_LEAVE_POLICY,
            resource    = "LeavePolicy",
            description = "Delete leave policy"
    )
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Policy deleted successfully")
                        .build()
        );
    }
}