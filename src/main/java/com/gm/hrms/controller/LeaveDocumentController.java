package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.dto.response.LeaveDocumentResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.LeaveDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/leave-documents")
@RequiredArgsConstructor
public class LeaveDocumentController {

    private final LeaveDocumentService service;

    // ================= UPLOAD =================
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN','HR')")
    @PostMapping("/{leaveId}")
    @Auditable(
            action      = AuditAction.UPLOAD_DOCUMENT,
            resource    = "LeaveDocument",
            description = "Upload documents for leave request"
    )
    public ResponseEntity<ApiResponse<Void>> upload(
            @PathVariable Long leaveId,
            @RequestParam Long personalId,
            @RequestParam List<MultipartFile> files) {

        service.upload(leaveId, personalId, files);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Documents uploaded successfully")
                        .build()
        );
    }

    // ================= GET =================
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN','HR')")
    @GetMapping("/{leaveId}")
    public ResponseEntity<ApiResponse<List<LeaveDocumentResponseDTO>>> get(
            @PathVariable Long leaveId) {

        return ResponseEntity.ok(
                ApiResponse.<List<LeaveDocumentResponseDTO>>builder()
                        .success(true)
                        .message("Documents fetched successfully")
                        .data(service.getByLeave(leaveId))
                        .build()
        );
    }

    // ================= DELETE =================
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN','HR')")
    @DeleteMapping("/{id}")
    @Auditable(
            action      = AuditAction.DELETE_DOCUMENT,
            resource    = "LeaveDocument",
            description = "Delete leave document"
    )
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Document deleted successfully")
                        .build()
        );
    }
}