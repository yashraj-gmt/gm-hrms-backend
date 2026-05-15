package com.gm.hrms.controller;

import com.gm.hrms.audit.*;
import com.gm.hrms.dto.request.ShiftAssignmentRequestDTO;
import com.gm.hrms.dto.response.*;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.ShiftAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/shift-assignments")
@RequiredArgsConstructor
public class ShiftAssignmentController {

    private final ShiftAssignmentService service;

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping
    @Auditable(action = AuditAction.ASSIGN_SHIFT, resource = "ShiftAssignment",
            description = "Assign shift to employees")
    public ResponseEntity<ApiResponse<List<ShiftAssignmentResponseDTO>>> assign(
            @Valid @RequestBody ShiftAssignmentRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.<List<ShiftAssignmentResponseDTO>>builder()
                .success(true).message("Shift assigned successfully")
                .data(service.assign(dto)).build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<ShiftAssignmentResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.<PageResponseDTO<ShiftAssignmentResponseDTO>>builder()
                .success(true).data(service.getAll(
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))))
                .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/eligible-persons")
    public ResponseEntity<ApiResponse<List<EligiblePersonDTO>>> searchEligiblePersons(
            @RequestParam(defaultValue = "") String search) {
        return ResponseEntity.ok(ApiResponse.<List<EligiblePersonDTO>>builder()
                .success(true).data(service.searchEligiblePersons(search)).build());
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','INTERN','TRAINEE','ADMIN','HR')")
    @GetMapping("/my-current")
    public ResponseEntity<ApiResponse<CurrentShiftResponseDTO>> getMyCurrentShift() {
        return ResponseEntity.ok(ApiResponse.<CurrentShiftResponseDTO>builder()
                .success(true).data(service.getMyCurrentShift()).build());
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','INTERN','TRAINEE','ADMIN','HR')")
    @GetMapping("/my-history")
    public ResponseEntity<ApiResponse<PageResponseDTO<ShiftAssignmentResponseDTO>>> getMyShiftHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
        return ResponseEntity.ok(ApiResponse.<PageResponseDTO<ShiftAssignmentResponseDTO>>builder()
                .success(true).data(service.getMyShiftHistory(PageRequest.of(page, size)))
                .build());
    }
}