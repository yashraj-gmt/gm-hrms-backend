package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.dto.request.AddressRequestDTO;
import com.gm.hrms.dto.response.AddressResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService service;

    // ================= CREATE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping
    @Auditable(
            action      = AuditAction.CREATE_ADDRESS,
            resource    = "Address",
            description = "Create employee address record"
    )
    public ResponseEntity<ApiResponse<AddressResponseDTO>> create(
            @Valid @RequestBody AddressRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.<AddressResponseDTO>builder()
                        .success(true)
                        .message("Address created successfully")
                        .data(service.create(dto))
                        .build()
        );
    }

    // ================= UPDATE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}")
    @Auditable(
            action      = AuditAction.UPDATE_ADDRESS,
            resource    = "Address",
            description = "Update employee address record"
    )
    public ResponseEntity<ApiResponse<AddressResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.<AddressResponseDTO>builder()
                        .success(true)
                        .message("Address updated successfully")
                        .data(service.update(id, dto))
                        .build()
        );
    }

    // ================= GET BY ID =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponseDTO>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.<AddressResponseDTO>builder()
                        .success(true)
                        .message("Address fetched successfully")
                        .data(service.getById(id))
                        .build()
        );
    }

    // ================= GET ALL =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<AddressResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<AddressResponseDTO>>builder()
                        .success(true)
                        .message("Addresses fetched successfully")
                        .data(service.getAll(PageRequest.of(page, size)))
                        .build()
        );
    }

    // ================= DELETE =================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Auditable(
            action      = AuditAction.DELETE_ADDRESS,
            resource    = "Address",
            description = "Delete employee address record"
    )
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Address deleted successfully")
                        .build()
        );
    }
}