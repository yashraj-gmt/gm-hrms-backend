package com.gm.hrms.controller;

import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.dto.response.TraineeResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.TraineeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/trainees")
@RequiredArgsConstructor
public class TraineeController {

    private final TraineeService service;

    // =========================================================================
    // UPDATE  (multipart — same pattern as Employee / Intern)
    // =========================================================================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<TraineeResponseDTO>> update(
            @PathVariable Long id,
            @RequestParam("trainee")                           String                      traineeJson,
            @RequestParam(required = false)                    MultipartFile               profileImage,
            @RequestParam(required = false)                    Map<String, MultipartFile>  documents,
            @RequestParam(required = false)                    Map<String, String>         reasons
    ) throws Exception {

        return ResponseEntity.ok(ApiResponse.<TraineeResponseDTO>builder()
                .success(true)
                .message("Trainee updated successfully")
                .data(service.update(id, traineeJson, profileImage, documents, reasons))
                .build());
    }

    // =========================================================================
    // GET BY ID
    // =========================================================================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TraineeResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<TraineeResponseDTO>builder()
                .success(true)
                .message("Trainee fetched successfully")
                .data(service.getById(id))
                .build());
    }

    // =========================================================================
    // GET ALL
    // =========================================================================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<TraineeResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(ApiResponse.<PageResponseDTO<TraineeResponseDTO>>builder()
                .success(true)
                .message("Trainees fetched successfully")
                .data(service.getAll(PageRequest.of(page, size)))
                .build());
    }

    // =========================================================================
    // DELETE  — ADMIN only
    // =========================================================================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Trainee deactivated successfully")
                .build());
    }
}