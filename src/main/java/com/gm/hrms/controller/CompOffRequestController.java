package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.dto.request.CompOffRequestDTO;
import com.gm.hrms.dto.response.CompOffResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.CompOffRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comp-off-requests")
@RequiredArgsConstructor
public class CompOffRequestController {

    private final CompOffRequestService service;

    // ================= APPLY =================
    @PostMapping
    @Auditable(
            action      = AuditAction.APPLY_COMP_OFF,
            resource    = "CompOffRequest",
            description = "Employee applies for comp-off"
    )
    public ResponseEntity<ApiResponse<CompOffResponseDTO>> apply(
            @RequestBody CompOffRequestDTO request) {

        return ResponseEntity.ok(
                ApiResponse.<CompOffResponseDTO>builder()
                        .success(true)
                        .message("Comp-off applied successfully")
                        .data(service.apply(request))
                        .build()
        );
    }

    // ================= APPROVE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}/approve")
    @Auditable(
            action      = AuditAction.APPROVE_COMP_OFF,
            resource    = "CompOffRequest",
            description = "Approve comp-off request"
    )
    public ResponseEntity<ApiResponse<CompOffResponseDTO>> approve(
            @PathVariable Long id,
            @RequestParam Long approverId) {

        return ResponseEntity.ok(
                ApiResponse.<CompOffResponseDTO>builder()
                        .success(true)
                        .message("Comp-off approved successfully")
                        .data(service.approve(id, approverId))
                        .build()
        );
    }

    // ================= REJECT =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}/reject")
    @Auditable(
            action      = AuditAction.REJECT_COMP_OFF,
            resource    = "CompOffRequest",
            description = "Reject comp-off request"
    )
    public ResponseEntity<ApiResponse<CompOffResponseDTO>> reject(
            @PathVariable Long id,
            @RequestParam Long approverId) {

        return ResponseEntity.ok(
                ApiResponse.<CompOffResponseDTO>builder()
                        .success(true)
                        .message("Comp-off rejected successfully")
                        .data(service.reject(id, approverId))
                        .build()
        );
    }

    // ================= GET BY USER =================
    @GetMapping("/user/{personalId}")
    public ResponseEntity<ApiResponse<List<CompOffResponseDTO>>> getByUser(
            @PathVariable Long personalId) {

        return ResponseEntity.ok(
                ApiResponse.<List<CompOffResponseDTO>>builder()
                        .success(true)
                        .message("Comp-off requests fetched successfully")
                        .data(service.getByUser(personalId))
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<CompOffResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<CompOffResponseDTO>>builder()
                        .success(true)
                        .message("Comp-off requests fetched successfully")
                        .data(service.getAll(PageRequest.of(page, size)))
                        .build()
        );
    }
}