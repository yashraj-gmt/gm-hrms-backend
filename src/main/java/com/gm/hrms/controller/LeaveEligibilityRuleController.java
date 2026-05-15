package com.gm.hrms.controller;

import com.gm.hrms.dto.request.LeaveEligibilityRuleRequestDTO;
import com.gm.hrms.dto.response.LeaveEligibilityRuleResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.LeaveEligibilityRuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-eligibility-rules")
@RequiredArgsConstructor
public class LeaveEligibilityRuleController {

    private final LeaveEligibilityRuleService service;

    // ================= CREATE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping
    public ResponseEntity<ApiResponse<LeaveEligibilityRuleResponseDTO>> create(
            @Valid @RequestBody LeaveEligibilityRuleRequestDTO request) {

        return ResponseEntity.ok(
                ApiResponse.<LeaveEligibilityRuleResponseDTO>builder()
                        .success(true)
                        .message("Eligibility rule created successfully")
                        .data(service.create(request))
                        .build()
        );
    }

    // ================= GET =================
    @GetMapping("/policy/{policyId}")
    public ResponseEntity<ApiResponse<LeaveEligibilityRuleResponseDTO>> getByPolicy(
            @PathVariable Long policyId) {

        return ResponseEntity.ok(
                ApiResponse.<LeaveEligibilityRuleResponseDTO>builder()
                        .success(true)
                        .message("Eligibility rule fetched successfully")
                        .data(service.getByPolicy(policyId))
                        .build()
        );
    }

    // ================= PATCH =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<LeaveEligibilityRuleResponseDTO>> patch(
            @PathVariable Long id,
            @RequestBody LeaveEligibilityRuleRequestDTO request) {

        return ResponseEntity.ok(
                ApiResponse.<LeaveEligibilityRuleResponseDTO>builder()
                        .success(true)
                        .message("Eligibility rule updated successfully")
                        .data(service.update(id, request))
                        .build()
        );
    }

    // ================= DELETE =================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Eligibility rule deleted successfully")
                        .build()
        );
    }
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<LeaveEligibilityRuleResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<LeaveEligibilityRuleResponseDTO>>builder()
                        .success(true)
                        .message("Eligibility rules fetched successfully")
                        .data(service.getAll(PageRequest.of(page, size)))
                        .build()
        );
    }
}