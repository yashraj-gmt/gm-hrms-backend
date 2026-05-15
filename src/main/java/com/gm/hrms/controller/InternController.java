package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.dto.request.InternUpdateDTO;
import com.gm.hrms.dto.response.InternResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.InternService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interns")
@RequiredArgsConstructor
public class InternController {

    private final InternService service;

    // ================= UPDATE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Auditable(
            action      = AuditAction.UPDATE_INTERN,
            resource    = "Intern",
            description = "Update intern record"
    )
    public ResponseEntity<ApiResponse<InternResponseDTO>> update(
            @PathVariable Long id,
            @RequestParam("intern") String internJson,
            @RequestParam(required = false) MultipartFile profileImage,
            @RequestParam(required = false) Map<String, MultipartFile> documents,
            @RequestParam(required = false) Map<String, String> reasons
    ) throws Exception {

        InternResponseDTO response =
                service.update(id, internJson, profileImage, documents, reasons);

        return ResponseEntity.ok(
                ApiResponse.<InternResponseDTO>builder()
                        .success(true)
                        .message("Intern updated successfully")
                        .data(service.update(id, internJson, profileImage, documents, reasons))
                        .build()
        );
    }

    // ================= GET BY ID =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InternResponseDTO>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.<InternResponseDTO>builder()
                        .success(true)
                        .message("Intern fetched successfully")
                        .data(service.getById(id))
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<InternResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<InternResponseDTO>>builder()
                        .success(true)
                        .message("Interns fetched successfully")
                        .data(service.getAll(PageRequest.of(page, size)))
                        .build()
        );
    }

    // ================= DELETE =================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Auditable(
            action      = AuditAction.DELETE_INTERN,
            resource    = "Intern",
            description = "Deactivate intern"
    )
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Intern deactivated successfully")
                        .build()
        );
    }
}
