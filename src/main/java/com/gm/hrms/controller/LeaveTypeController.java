package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.dto.request.LeaveTypeRequestDTO;
import com.gm.hrms.dto.response.LeaveTypeResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.LeaveTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave-types")
@RequiredArgsConstructor
public class LeaveTypeController {

    private final LeaveTypeService service;

    // ================= CREATE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping
    @Auditable(
            action      = AuditAction.CREATE_LEAVE_TYPE,
            resource    = "LeaveType",
            description = "Create leave type"
    )
    public ResponseEntity<ApiResponse<LeaveTypeResponseDTO>> create(
            @Valid @RequestBody LeaveTypeRequestDTO request) {

        return ResponseEntity.ok(
                ApiResponse.<LeaveTypeResponseDTO>builder()
                        .success(true)
                        .message("Leave type created successfully")
                        .data(service.create(request))
                        .build()
        );
    }

    // ================= GET BY ID =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeaveTypeResponseDTO>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.<LeaveTypeResponseDTO>builder()
                        .success(true)
                        .message("Leave type fetched successfully")
                        .data(service.getById(id))
                        .build()
        );
    }

    // ================= GET ALL =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<LeaveTypeResponseDTO>>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<LeaveTypeResponseDTO>>builder()
                        .success(true)
                        .message("Leave types fetched successfully")
                        .data(service.getAll(search, PageRequest.of(page, size)))
                        .build()
        );
    }

    // ================= PATCH =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}")
    @Auditable(
            action      = AuditAction.UPDATE_LEAVE_TYPE,
            resource    = "LeaveType",
            description = "Update leave type"
    )
    public ResponseEntity<ApiResponse<LeaveTypeResponseDTO>> update(
            @PathVariable Long id,
            @RequestBody LeaveTypeRequestDTO request) {

        return ResponseEntity.ok(
                ApiResponse.<LeaveTypeResponseDTO>builder()
                        .success(true)
                        .message("Leave type updated successfully")
                        .data(service.update(id, request))
                        .build()
        );
    }

    // ================= DELETE =================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Auditable(
            action      = AuditAction.DELETE_LEAVE_TYPE,
            resource    = "LeaveType",
            description = "Delete leave type"
    )
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Leave type deleted successfully")
                        .build()
        );
    }
}