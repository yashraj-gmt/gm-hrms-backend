package com.gm.hrms.controller;

import com.gm.hrms.audit.*;
import com.gm.hrms.dto.request.ShiftRequestDTO;
import com.gm.hrms.dto.response.*;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.ShiftService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
public class ShiftController {

    private final ShiftService service;

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping
    @Auditable(action = AuditAction.CREATE_SHIFT, resource = "Shift", description = "Create shift")
    public ResponseEntity<ApiResponse<ShiftResponseDTO>> create(
            @Valid @RequestBody ShiftRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.<ShiftResponseDTO>builder()
                .success(true).message("Shift created successfully")
                .data(service.create(dto)).build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}")
    @Auditable(action = AuditAction.UPDATE_SHIFT, resource = "Shift", description = "Update shift")
    public ResponseEntity<ApiResponse<ShiftResponseDTO>> update(
            @PathVariable Long id,
            @RequestBody ShiftRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.<ShiftResponseDTO>builder()
                .success(true).message("Shift updated successfully")
                .data(service.update(id, dto)).build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<ShiftResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<ShiftResponseDTO>>builder()
                        .success(true)
                        .message("Shifts fetched")
                        .data(service.getAll(pageable))
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShiftResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<ShiftResponseDTO>builder()
                .success(true).data(service.getById(id)).build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}/toggle-status")
    @Auditable(action = AuditAction.TOGGLE_SHIFT_STATUS, resource = "Shift",
            description = "Toggle shift active status")
    public ResponseEntity<ApiResponse<ShiftResponseDTO>> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<ShiftResponseDTO>builder()
                .success(true).message("Shift status updated")
                .data(service.toggleStatus(id)).build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @DeleteMapping("/{id}")
    @Auditable(action = AuditAction.DELETE_SHIFT, resource = "Shift", description = "Delete shift")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Shift deleted successfully").build());
    }
}