package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.dto.request.LeaveCancelRequestDTO;
import com.gm.hrms.dto.request.LeaveRequestDTO;
import com.gm.hrms.dto.response.LeaveRequestResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.LeaveRequestService;
import com.gm.hrms.service.impl.LeaveRequestServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-requests")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService service;

    // ================= APPLY =================
    @PreAuthorize("hasAnyRole('EMPLOYEE','INTERN','TRAINEE')")
    @PostMapping
    @Auditable(
            action      = AuditAction.APPLY_LEAVE,
            resource    = "LeaveRequest",
            description = "Employee applies for leave"
    )
    public ResponseEntity<ApiResponse<LeaveRequestResponseDTO>> apply(
            @Valid @RequestBody LeaveRequestDTO dto) {

        LeaveRequestResponseDTO response = service.apply(dto);

        return ResponseEntity.ok(
                ApiResponse.<LeaveRequestResponseDTO>builder()
                        .success(true)
                        .message("Leave applied successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= APPROVE =================
    @PreAuthorize("hasAnyRole('MANAGER','HR','ADMIN')")
    @PatchMapping("/{id}/approve")
    @Auditable(
            action      = AuditAction.APPROVE_LEAVE,
            resource    = "LeaveRequest",
            description = "Approve leave request"
    )
    public ResponseEntity<ApiResponse<Void>> approve(
            @PathVariable Long id,
            @RequestParam Long approverId) {

        service.approve(id, approverId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Leave approved successfully")
                        .build()
        );
    }

    // ================= REJECT =================
    @PreAuthorize("hasAnyRole('MANAGER','HR','ADMIN')")
    @PatchMapping("/{id}/reject")
    @Auditable(
            action      = AuditAction.REJECT_LEAVE,
            resource    = "LeaveRequest",
            description = "Reject leave request"
    )
    public ResponseEntity<ApiResponse<Void>> reject(
            @PathVariable Long id,
            @RequestParam String reason) {

        service.reject(id, reason);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Leave rejected successfully")
                        .build()
        );
    }

    // ================= CANCEL =================
    @PreAuthorize("hasAnyRole('EMPLOYEE','INTERN','TRAINEE','ADMIN','HR')")
    @PatchMapping("/{id}/cancel")
    @Auditable(action = AuditAction.CANCEL_LEAVE, resource = "LeaveRequest",
            description = "Cancel leave request with reason")
    public ResponseEntity<ApiResponse<Void>> cancel(
            @PathVariable Long id,
            @Valid @RequestBody LeaveCancelRequestDTO request) {

        ((LeaveRequestServiceImpl) service).cancelWithReason(id, request.getCancelReason());

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Leave cancelled successfully")
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','INTERN','TRAINEE','ADMIN','HR')")
    @GetMapping("/{personalId}")
    public ResponseEntity<ApiResponse<PageResponseDTO<LeaveRequestResponseDTO>>> getMyLeaves(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable Long personalId
            ) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<LeaveRequestResponseDTO>>builder()
                        .success(true)
                        .message("My leave requests fetched successfully")
                        .data(service.getMyLeaves(personalId, PageRequest.of(page, size)))
                        .build()
        );
    }



    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<LeaveRequestResponseDTO>>> getAllLeaves(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<LeaveRequestResponseDTO>>builder()
                        .success(true)
                        .message("All leave requests fetched successfully")
                        .data(service.getAll(PageRequest.of(page, size)))
                        .build()
        );
    }

    // ================= REQUEST DOCUMENT =================
    @PreAuthorize("hasAnyRole('MANAGER','HR','ADMIN')")
    @PatchMapping("/{id}/request-document")
    @Auditable(
            action      = AuditAction.REQUEST_DOCUMENT,
            resource    = "LeaveRequest",
            description = "Request supporting document for leave"
    )
    public ResponseEntity<ApiResponse<Void>> requestDocument(@PathVariable Long id) {

        service.requestDocument(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Document requested successfully")
                        .build()
        );
    }
}