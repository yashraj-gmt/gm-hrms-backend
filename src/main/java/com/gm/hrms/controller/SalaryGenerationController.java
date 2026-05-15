package com.gm.hrms.controller;

import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.audit.Auditable;
import com.gm.hrms.dto.request.SalaryGenerationRequestDTO;
import com.gm.hrms.dto.response.SalaryGenerationResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.SalaryGenerationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll/generate")
@RequiredArgsConstructor
public class SalaryGenerationController {

    private final SalaryGenerationService service;

    /** Step 1 — Generate (creates DRAFT slips) */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Auditable(action = AuditAction.GENERATE_SALARY,
            resource = "SalaryGeneration", description = "Run monthly salary generation")
    public ResponseEntity<ApiResponse<SalaryGenerationResponseDTO>> generate(
            @Valid @RequestBody SalaryGenerationRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.<SalaryGenerationResponseDTO>builder()
                .success(true).message("Salary generated successfully")
                .data(service.generate(dto)).build());
    }

    /** Step 2 — Finalize (locks slips, enables PDF download) */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/finalize")
    @Auditable(action = AuditAction.FINALIZE_SALARY,
            resource = "SalaryGeneration", description = "Finalize monthly salary")
    public ResponseEntity<ApiResponse<SalaryGenerationResponseDTO>> finalize(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<SalaryGenerationResponseDTO>builder()
                .success(true).message("Salary finalized successfully")
                .data(service.finalizeSalary(id)).build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SalaryGenerationResponseDTO>> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<SalaryGenerationResponseDTO>builder()
                .success(true).data(service.getById(id)).build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<SalaryGenerationResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<SalaryGenerationResponseDTO>>builder()
                .success(true).data(service.getAll()).build());
    }
}