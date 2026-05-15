package com.gm.hrms.controller;

import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.audit.Auditable;
import com.gm.hrms.dto.request.SalaryStructureRequestDTO;
import com.gm.hrms.dto.response.SalaryStructureResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.SalaryStructureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll/salary-structure")
@RequiredArgsConstructor
public class SalaryStructureController {

    private final SalaryStructureService service;

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping("/{personalId}")
    @Auditable(action = AuditAction.CREATE_SALARY_STRUCTURE,
            resource = "SalaryStructure", description = "Create employee salary structure")
    public ResponseEntity<ApiResponse<SalaryStructureResponseDTO>> create(
            @PathVariable Long personalId,
            @Valid @RequestBody SalaryStructureRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.<SalaryStructureResponseDTO>builder()
                .success(true).message("Salary structure created")
                .data(service.create(personalId, dto)).build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{structureId}")
    @Auditable(action = AuditAction.UPDATE_SALARY_STRUCTURE,
            resource = "SalaryStructure", description = "Update salary structure")
    public ResponseEntity<ApiResponse<SalaryStructureResponseDTO>> update(
            @PathVariable Long structureId,
            @Valid @RequestBody SalaryStructureRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.<SalaryStructureResponseDTO>builder()
                .success(true).message("Salary structure updated")
                .data(service.update(structureId, dto)).build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/{personalId}/active")
    public ResponseEntity<ApiResponse<SalaryStructureResponseDTO>> getActive(
            @PathVariable Long personalId) {
        return ResponseEntity.ok(ApiResponse.<SalaryStructureResponseDTO>builder()
                .success(true).data(service.getActive(personalId)).build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/{personalId}/history")
    public ResponseEntity<ApiResponse<List<SalaryStructureResponseDTO>>> getHistory(
            @PathVariable Long personalId) {
        return ResponseEntity.ok(ApiResponse.<List<SalaryStructureResponseDTO>>builder()
                .success(true).data(service.getHistory(personalId)).build());
    }
}