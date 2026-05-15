package com.gm.hrms.controller;

import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.audit.Auditable;
import com.gm.hrms.dto.request.PayrollComponentRequestDTO;
import com.gm.hrms.dto.response.PayrollComponentResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.PayrollComponentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll/components")
@RequiredArgsConstructor
public class PayrollComponentController {

    private final PayrollComponentService service;

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping
    @Auditable(action = AuditAction.CREATE_PAYROLL_COMPONENT,
            resource = "PayrollComponent", description = "Create payroll component")
    public ResponseEntity<ApiResponse<PayrollComponentResponseDTO>> create(
            @Valid @RequestBody PayrollComponentRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.<PayrollComponentResponseDTO>builder()
                .success(true).message("Component created").data(service.create(dto)).build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}")
    @Auditable(action = AuditAction.UPDATE_PAYROLL_COMPONENT,
                resource = "PayrollComponent",
                description = "Update payroll component")
    public ResponseEntity<ApiResponse<PayrollComponentResponseDTO>> update(
            @PathVariable Long id, @RequestBody PayrollComponentRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.<PayrollComponentResponseDTO>builder()
                .success(true).message("Component updated").data(service.update(id, dto)).build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PayrollComponentResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<PayrollComponentResponseDTO>builder()
                .success(true).data(service.getById(id)).build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<PayrollComponentResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<PayrollComponentResponseDTO>>builder()
                .success(true).data(service.getAll()).build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Auditable(action = AuditAction.DELETE_PAYROLL_COMPONENT,
            resource = "PayrollComponent", description = "Deactivate payroll component")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Component deactivated").build());
    }
}