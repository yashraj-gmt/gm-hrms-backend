package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.dto.request.ProjectRequestDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.dto.response.ProjectResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    // ================= CREATE =================
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Auditable(
            action      = AuditAction.CREATE_PROJECT,
            resource    = "Project",
            description = "Create new project"
    )
    public ResponseEntity<ApiResponse<?>> create(@RequestBody ProjectRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Project created successfully")
                        .data(projectService.create(dto))
                        .build()
        );
    }

    // ================= ADMIN + HR =================
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<ApiResponse<PageResponseDTO<ProjectResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<ProjectResponseDTO>>builder()
                        .success(true)
                        .message("Projects fetched successfully")
                        .data(projectService.getAll(PageRequest.of(page, size)))
                        .build()
        );
    }

    // ================= ADMIN + HR =================
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable Long id){

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Project fetched successfully")
                        .data(projectService.getById(id))
                        .build()
        );
    }

    // ================= UPDATE =================
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Auditable(
            action      = AuditAction.UPDATE_PROJECT,
            resource    = "Project",
            description = "Update project details"
    )
    public ResponseEntity<ApiResponse<?>> update(
            @PathVariable Long id,
            @RequestBody ProjectRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Project updated successfully")
                        .data(projectService.update(id, dto))
                        .build()
        );
    }

    // ================= ADMIN ONLY =================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Auditable(
            action      = AuditAction.DELETE_PROJECT,
            resource    = "Project",
            description = "Delete project"
    )
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id) {

        projectService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Project deleted successfully")
                        .build()
        );
    }
}