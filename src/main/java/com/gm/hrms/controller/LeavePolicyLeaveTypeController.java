package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.dto.request.LeavePolicyLeaveTypeRequestDTO;
import com.gm.hrms.dto.response.LeavePolicyLeaveTypeResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.LeavePolicyLeaveTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/policy-mapping")
@RequiredArgsConstructor
public class LeavePolicyLeaveTypeController {

    private final LeavePolicyLeaveTypeService service;

    // ================= CREATE =================
    @PostMapping
    @Auditable(
            action      = AuditAction.CREATE_POLICY_MAPPING,
            resource    = "PolicyMapping",
            description = "Map leave type to leave policy"
    )
    public ResponseEntity<ApiResponse<LeavePolicyLeaveTypeResponseDTO>> create(
            @Valid @RequestBody LeavePolicyLeaveTypeRequestDTO request) {

        return ResponseEntity.ok(
                ApiResponse.<LeavePolicyLeaveTypeResponseDTO>builder()
                        .success(true)
                        .message("Mapping created successfully")
                        .data(service.create(request))
                        .build()
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeavePolicyLeaveTypeResponseDTO>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.<LeavePolicyLeaveTypeResponseDTO>builder()
                        .success(true)
                        .message("Mapping fetched successfully")
                        .data(service.getById(id))
                        .build()
        );
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<LeavePolicyLeaveTypeResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<LeavePolicyLeaveTypeResponseDTO>>builder()
                        .success(true)
                        .message("Mappings fetched successfully")
                        .data(service.getAll(PageRequest.of(page, size)))
                        .build()
        );
    }

    // ================= PATCH =================
    @PatchMapping("/{id}")
    @Auditable(
            action      = AuditAction.UPDATE_POLICY_MAPPING,
            resource    = "PolicyMapping",
            description = "Update policy-leave type mapping"
    )
    public ResponseEntity<ApiResponse<LeavePolicyLeaveTypeResponseDTO>> update(
            @PathVariable Long id,
            @RequestBody LeavePolicyLeaveTypeRequestDTO request) {

        return ResponseEntity.ok(
                ApiResponse.<LeavePolicyLeaveTypeResponseDTO>builder()
                        .success(true)
                        .message("Mapping updated successfully")
                        .data(service.patchUpdate(id, request))
                        .build()
        );
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    @Auditable(
            action      = AuditAction.DELETE_POLICY_MAPPING,
            resource    = "PolicyMapping",
            description = "Delete policy-leave type mapping"
    )
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Mapping deleted successfully")
                        .build()
        );
    }
}