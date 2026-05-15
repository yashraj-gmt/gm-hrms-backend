package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.dto.request.CompOffRuleRequestDTO;
import com.gm.hrms.dto.response.CompOffRuleResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.CompOffRuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comp-off-rules")
@RequiredArgsConstructor
public class CompOffRuleController {

    private final CompOffRuleService service;

    // ================= CREATE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping
    @Auditable(
            action      = AuditAction.CREATE_COMP_OFF_RULE,
            resource    = "CompOffRule",
            description = "Create comp-off rule"
    )
    public ResponseEntity<ApiResponse<CompOffRuleResponseDTO>> create(
            @Valid @RequestBody CompOffRuleRequestDTO request) {

        return ResponseEntity.ok(
                ApiResponse.<CompOffRuleResponseDTO>builder()
                        .success(true)
                        .message("Comp-off rule created successfully")
                        .data(service.create(request))
                        .build()
        );
    }

    // ================= GET BY POLICY =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/policy/{policyId}")
    public ResponseEntity<ApiResponse<CompOffRuleResponseDTO>> getByPolicy(
            @PathVariable Long policyId) {

        return ResponseEntity.ok(
                ApiResponse.<CompOffRuleResponseDTO>builder()
                        .success(true)
                        .message("Comp-off rule fetched successfully")
                        .data(service.getByPolicy(policyId))
                        .build()
        );
    }

    // ================= PATCH =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}")
    @Auditable(
            action      = AuditAction.UPDATE_COMP_OFF_RULE,
            resource    = "CompOffRule",
            description = "Update comp-off rule"
    )
    public ResponseEntity<ApiResponse<CompOffRuleResponseDTO>> update(
            @PathVariable Long id,
            @RequestBody CompOffRuleRequestDTO request) {

        return ResponseEntity.ok(
                ApiResponse.<CompOffRuleResponseDTO>builder()
                        .success(true)
                        .message("Comp-off rule updated successfully")
                        .data(service.patchUpdate(id, request))
                        .build()
        );
    }

    // ================= DELETE =================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Auditable(
            action      = AuditAction.DELETE_COMP_OFF_RULE,
            resource    = "CompOffRule",
            description = "Delete comp-off rule"
    )
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Comp-off rule deactivated successfully")
                        .build()
        );
    }
}