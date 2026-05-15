package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.dto.request.CarryForwardRuleRequestDTO;
import com.gm.hrms.dto.response.CarryForwardRuleResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.CarryForwardRuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carry-forward-rules")
@RequiredArgsConstructor
public class CarryForwardRuleController {

    private final CarryForwardRuleService service;

    // ================= CREATE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping
    @Auditable(
            action      = AuditAction.CREATE_CARRY_FORWARD_RULE,
            resource    = "CarryForwardRule",
            description = "Create carry forward rule"
    )
    public ResponseEntity<ApiResponse<CarryForwardRuleResponseDTO>> create(
            @Valid @RequestBody CarryForwardRuleRequestDTO request) {

        return ResponseEntity.ok(
                ApiResponse.<CarryForwardRuleResponseDTO>builder()
                        .success(true)
                        .message("Carry forward rule created successfully")
                        .data(service.create(request))
                        .build()
        );
    }

    // ================= GET BY POLICY =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/policy/{policyId}")
    public ResponseEntity<ApiResponse<CarryForwardRuleResponseDTO>> getByPolicy(
            @PathVariable Long policyId) {

        return ResponseEntity.ok(
                ApiResponse.<CarryForwardRuleResponseDTO>builder()
                        .success(true)
                        .message("Carry forward rule fetched successfully")
                        .data(service.getByPolicy(policyId))
                        .build()
        );
    }

    // ================= PATCH =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}")
    @Auditable(
            action      = AuditAction.UPDATE_CARRY_FORWARD_RULE,
            resource    = "CarryForwardRule",
            description = "Update carry forward rule"
    )
    public ResponseEntity<ApiResponse<CarryForwardRuleResponseDTO>> update(
            @PathVariable Long id,
            @RequestBody CarryForwardRuleRequestDTO request) {

        return ResponseEntity.ok(
                ApiResponse.<CarryForwardRuleResponseDTO>builder()
                        .success(true)
                        .message("Carry forward rule updated successfully")
                        .data(service.patchUpdate(id, request))
                        .build()
        );
    }

    // ================= DELETE =================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Auditable(
            action      = AuditAction.DELETE_CARRY_FORWARD_RULE,
            resource    = "CarryForwardRule",
            description = "Delete carry forward rule"
    )
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Carry forward rule deleted successfully")
                        .build()
        );
    }
}