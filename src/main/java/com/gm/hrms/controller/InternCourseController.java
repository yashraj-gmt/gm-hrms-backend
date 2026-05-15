package com.gm.hrms.controller;

import com.gm.hrms.dto.request.InternCourseRequestDTO;
import com.gm.hrms.dto.response.InternCourseResponseDTO;
import com.gm.hrms.dto.response.InternCourseStatsDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.InternCourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/intern-courses")
@RequiredArgsConstructor
public class InternCourseController {

    private final InternCourseService service;

    // ── CREATE → ADMIN only ───────────────────────────────────────────────────
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<InternCourseResponseDTO>> create(
            @Valid @RequestBody InternCourseRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.<InternCourseResponseDTO>builder()
                        .success(true)
                        .message("Course created successfully")
                        .data(service.createCourse(dto))
                        .build()
        );
    }

    // ── UPDATE (PATCH) → ADMIN only ───────────────────────────────────────────
    // Send { status: true } to re-activate a soft-deleted course
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<InternCourseResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody InternCourseRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.<InternCourseResponseDTO>builder()
                        .success(true)
                        .message("Course updated successfully")
                        .data(service.updateCourse(id, dto))
                        .build()
        );
    }

    // ── GET ALL (active + inactive) → ADMIN + HR ──────────────────────────────
    // Returns ALL courses regardless of status, sorted by id desc
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<InternCourseResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<InternCourseResponseDTO>>builder()
                        .success(true)
                        .message("Courses fetched successfully")
                        .data(service.getAllCourses(
                                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))
                        ))
                        .build()
        );
    }

    // ── GET BY ID → ADMIN + HR ────────────────────────────────────────────────
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InternCourseResponseDTO>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.<InternCourseResponseDTO>builder()
                        .success(true)
                        .message("Course fetched successfully")
                        .data(service.getCourseById(id))
                        .build()
        );
    }

    // ── SOFT DELETE → ADMIN only ──────────────────────────────────────────────
    // Sets status = false. Record stays in DB and appears in listing as Inactive.
    // Re-activate via PATCH /{id} with body { "status": true }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        service.deleteCourse(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Course deactivated successfully")
                        .build()
        );
    }

    // ── STATS → ADMIN + HR ────────────────────────────────────────────────────
    // GET /api/intern-courses/stats
    // Returns { total, active, inactive }
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<InternCourseStatsDTO>> getStats() {

        return ResponseEntity.ok(
                ApiResponse.<InternCourseStatsDTO>builder()
                        .success(true)
                        .message("Stats fetched successfully")
                        .data(service.getStats())
                        .build()
        );
    }
}