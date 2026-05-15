package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.dto.request.PersonalInformationRequestDTO;
import com.gm.hrms.dto.response.PersonalInformationResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.PersonalInformationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public class PersonalInformationController {

    private final PersonalInformationService service;

    // ================= CREATE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping
    @Auditable(
            action      = AuditAction.CREATE_PERSON,
            resource    = "PersonalInformation",
            description = "Create personal information record"
    )
    public ResponseEntity<ApiResponse<PersonalInformationResponseDTO>> create(
            @Valid @RequestBody PersonalInformationRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.<PersonalInformationResponseDTO>builder()
                        .success(true)
                        .message("PersonalInformation created successfully")
                        .data(service.create(dto))
                        .build()
        );
    }

    // ================= GET BY ID =================
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE','INTERN')")
    @GetMapping("/{id}")
    @Auditable(
            action      = AuditAction.VIEW_PERSON,
            resource    = "PersonalInformation",
            description = "View personal information record"
    )
    public ResponseEntity<ApiResponse<PersonalInformationResponseDTO>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.<PersonalInformationResponseDTO>builder()
                        .success(true)
                        .message("PersonalInformation fetched successfully")
                        .data(service.getById(id))
                        .build()
        );
    }
}