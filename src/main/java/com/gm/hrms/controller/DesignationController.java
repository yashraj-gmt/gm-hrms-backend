package com.gm.hrms.controller;

import com.gm.hrms.dto.request.DesignationRequestDTO;
import com.gm.hrms.dto.response.DesignationResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.DesignationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/designations")
@RequiredArgsConstructor
public class DesignationController {

    private final DesignationService service;

    //  CREATE → Only ADMIN + HR
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(
            @Valid @RequestBody DesignationRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Designation created successfully")
                        .data(service.create(dto))
                        .build()
        );
    }

    //  GET BY ID → ADMIN + HR + EMPLOYEE
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Designation fetched successfully")
                        .data(service.getById(id))
                        .build()
        );
    }

    //  GET ALL (Pagination) → ADMIN + HR + EMPLOYEE
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<DesignationResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<DesignationResponseDTO>>builder()
                        .success(true)
                        .message("Designations fetched successfully")
                        .data(service.getAll(PageRequest.of(page, size)))
                        .build()
        );
    }

    //  UPDATE → Only ADMIN + HR
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> update(
            @PathVariable Long id,
            @Valid @RequestBody DesignationRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Designation updated successfully")
                        .data(service.update(id, dto))
                        .build()
        );
    }

    //  DELETE → Only ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Designation deleted successfully")
                        .build()
        );
    }
}