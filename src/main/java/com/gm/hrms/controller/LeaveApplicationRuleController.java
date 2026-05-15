package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.dto.request.LeaveApplicationRuleRequestDTO;
import com.gm.hrms.dto.response.LeaveApplicationRuleResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.LeaveApplicationRuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-application-rules")
@RequiredArgsConstructor
public class LeaveApplicationRuleController {

    private final LeaveApplicationRuleService service;

    // ================= CREATE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping
    @Auditable(
            action      = AuditAction.CREATE_LEAVE_RULE,
            resource    = "LeaveApplicationRule",
            description = "Create leave application rule"
    )
    public ResponseEntity<ApiResponse<LeaveApplicationRuleResponseDTO>> create(
            @Valid @RequestBody LeaveApplicationRuleRequestDTO request) {

        return ResponseEntity.ok(
                ApiResponse.<LeaveApplicationRuleResponseDTO>builder()
                        .success(true)
                        .message("Application rule created successfully")
                        .data(service.create(request))
                        .build()
        );
    }

    // ================= GET BY POLICY =================
    @GetMapping("/policy/{policyId}")
    public ResponseEntity<ApiResponse<LeaveApplicationRuleResponseDTO>> getByPolicy(
            @PathVariable Long policyId) {

        return ResponseEntity.ok(
                ApiResponse.<LeaveApplicationRuleResponseDTO>builder()
                        .success(true)
                        .message("Application rule fetched successfully")
                        .data(service.getByPolicy(policyId))
                        .build()
        );
    }

    // ================= GET ALL =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<LeaveApplicationRuleResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<LeaveApplicationRuleResponseDTO>>builder()
                        .success(true)
                        .message("Application rules fetched successfully")
                        .data(service.getAll(PageRequest.of(page, size)))
                        .build()
        );
    }

    // ================= PATCH =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}")
    @Auditable(
            action      = AuditAction.UPDATE_LEAVE_RULE,
            resource    = "LeaveApplicationRule",
            description = "Update leave application rule"
    )
    public ResponseEntity<ApiResponse<LeaveApplicationRuleResponseDTO>> patch(
            @PathVariable Long id,
            @RequestBody LeaveApplicationRuleRequestDTO request) {

        return ResponseEntity.ok(
                ApiResponse.<LeaveApplicationRuleResponseDTO>builder()
                        .success(true)
                        .message("Application rule updated successfully")
                        .data(service.patchUpdate(id, request))
                        .build()
        );
    }

    // ================= DELETE =================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Auditable(
            action      = AuditAction.DELETE_LEAVE_RULE,
            resource    = "LeaveApplicationRule",
            description = "Delete leave application rule"
    )
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Application rule deleted successfully")
                        .build()
        );
    }
}