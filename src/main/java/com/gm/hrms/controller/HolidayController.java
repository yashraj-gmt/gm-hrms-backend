package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.dto.request.HolidayRequestDTO;
import com.gm.hrms.dto.response.HolidayResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.HolidayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
public class HolidayController {

    private final HolidayService service;

    // ================= CREATE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping
    @Auditable(
            action      = AuditAction.CREATE_HOLIDAY,
            resource    = "Holiday",
            description = "Create holiday — affects org-wide leave calendar"
    )
    public ResponseEntity<ApiResponse<HolidayResponseDTO>> create(
            @Valid @RequestBody HolidayRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.<HolidayResponseDTO>builder()
                        .success(true)
                        .message("Holiday created successfully")
                        .data(service.create(dto))
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<HolidayResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<HolidayResponseDTO>>builder()
                        .success(true)
                        .message("Holidays fetched successfully")
                        .data(service.getAll(PageRequest.of(page, size)))
                        .build()
        );
    }

    // ================= GET BY ID =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HolidayResponseDTO>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.<HolidayResponseDTO>builder()
                        .success(true)
                        .data(service.getById(id))
                        .build()
        );
    }

    // ================= UPDATE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}")
    @Auditable(
            action      = AuditAction.UPDATE_HOLIDAY,
            resource    = "Holiday",
            description = "Update holiday — affects org-wide leave calendar"
    )
    public ResponseEntity<ApiResponse<HolidayResponseDTO>> update(
            @PathVariable Long id,
            @RequestBody HolidayRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.<HolidayResponseDTO>builder()
                        .success(true)
                        .message("Holiday updated successfully")
                        .data(service.update(id, dto))
                        .build()
        );
    }

    // ================= DELETE =================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Auditable(
            action      = AuditAction.DELETE_HOLIDAY,
            resource    = "Holiday",
            description = "Delete holiday"
    )
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Holiday deleted successfully")
                        .build()
        );
    }
}