package com.gm.hrms.controller;

import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.audit.Auditable;
import com.gm.hrms.dto.response.SalarySlipResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.SalarySlipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll/salary-slips")
@RequiredArgsConstructor
public class SalarySlipController {

    private final SalarySlipService service;

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SalarySlipResponseDTO>> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<SalarySlipResponseDTO>builder()
                .success(true).data(service.getById(id)).build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
    @GetMapping("/employee/{personalId}")
    public ResponseEntity<ApiResponse<List<SalarySlipResponseDTO>>> getByEmployee(
            @PathVariable Long personalId) {
        return ResponseEntity.ok(ApiResponse.<List<SalarySlipResponseDTO>>builder()
                .success(true).data(service.getByEmployee(personalId)).build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
    @GetMapping("/employee/{personalId}/{year}/{month}")
    public ResponseEntity<ApiResponse<SalarySlipResponseDTO>> getByPersonAndMonth(
            @PathVariable Long personalId,
            @PathVariable Integer year,
            @PathVariable Integer month) {
        return ResponseEntity.ok(ApiResponse.<SalarySlipResponseDTO>builder()
                .success(true).data(service.getByPersonAndMonth(personalId, month, year)).build());
    }

    /** Download PDF — returns raw bytes as application/pdf */
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
    @GetMapping("/{id}/download")
    @Auditable(action = AuditAction.DOWNLOAD_SALARY_SLIP,
            resource = "SalarySlip", description = "Download salary slip PDF")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        byte[] pdf = service.downloadPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=payslip-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
