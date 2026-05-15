package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.config.CustomUserDetails;
import com.gm.hrms.dto.request.BankLegalDetailsRequestDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.BankLegalDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bank-legal")
@RequiredArgsConstructor
public class BankLegalDetailsController {

    private final BankLegalDetailsService service;

    // ================= SAVE OR UPDATE =================
    @PostMapping("/{personalInformationId}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @Auditable(
            action      = AuditAction.SAVE_BANK_DETAILS,
            resource    = "BankLegalDetails",
            description = "Admin/HR saves or updates bank and legal details"
    )
    public ResponseEntity<ApiResponse<?>> saveOrUpdate(
            @PathVariable Long personalInformationId,
            @Valid @RequestBody BankLegalDetailsRequestDTO requestDTO) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Bank details saved successfully")
                        .data(service.saveOrUpdate(personalInformationId, requestDTO))
                        .build()
        );
    }

    // ================= GET MY DETAILS =================
    @GetMapping("/my-details")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<?>> getMyDetails(
            @AuthenticationPrincipal CustomUserDetails user) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Bank details fetched successfully")
                        .data(service.getMyDetails(user.getUserId()))
                        .build()
        );
    }
}