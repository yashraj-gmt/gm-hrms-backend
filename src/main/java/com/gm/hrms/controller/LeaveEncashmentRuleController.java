package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.dto.request.LeaveEncashmentRuleRequestDTO;
import com.gm.hrms.dto.response.LeaveEncashmentRuleResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.LeaveEncashmentRuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave-encashment-rules")
@RequiredArgsConstructor
public class LeaveEncashmentRuleController {

    private final LeaveEncashmentRuleService service;

    // ================= CREATE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping
    @Auditable(
            action      = AuditAction.CREATE_ENCASHMENT_RULE,
            resource    = "LeaveEncashmentRule",
            description = "Create leave encashment rule"
    )
    public ResponseEntity<ApiResponse<LeaveEncashmentRuleResponseDTO>> create(
            @Valid @RequestBody LeaveEncashmentRuleRequestDTO request) {

        return ResponseEntity.ok(
                ApiResponse.<LeaveEncashmentRuleResponseDTO>builder()
                        .success(true)
                        .message("Leave encashment rule created successfully")
                        .data(service.create(request))
                        .build()
        );
    }

    // ================= GET =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/policy/{policyId}")
    public ResponseEntity<ApiResponse<LeaveEncashmentRuleResponseDTO>> getByPolicy(
            @PathVariable Long policyId) {

        return ResponseEntity.ok(
                ApiResponse.<LeaveEncashmentRuleResponseDTO>builder()
                        .success(true)
                        .message("Leave encashment rule fetched successfully")
                        .data(service.getByPolicy(policyId))
                        .build()
        );
    }

    // ================= PATCH =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}")
    @Auditable(
            action      = AuditAction.UPDATE_ENCASHMENT_RULE,
            resource    = "LeaveEncashmentRule",
            description = "Update leave encashment rule"
    )
    public ResponseEntity<ApiResponse<LeaveEncashmentRuleResponseDTO>> update(
            @PathVariable Long id,
            @RequestBody LeaveEncashmentRuleRequestDTO request) {

        return ResponseEntity.ok(
                ApiResponse.<LeaveEncashmentRuleResponseDTO>builder()
                        .success(true)
                        .message("Leave encashment rule updated successfully")
                        .data(service.patchUpdate(id, request))
                        .build()
        );
    }

    // ================= DELETE =================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Auditable(
            action      = AuditAction.DELETE_ENCASHMENT_RULE,
            resource    = "LeaveEncashmentRule",
            description = "Delete leave encashment rule"
    )
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Leave encashment rule deleted successfully")
                        .build()
        );
    }


    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<LeaveEncashmentRuleResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<LeaveEncashmentRuleResponseDTO>>builder()
                        .success(true)
                        .message("Encashment rules fetched successfully")
                        .data(service.getAll(PageRequest.of(page, size)))
                        .build()
        );
    }
}