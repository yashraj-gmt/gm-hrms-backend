package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.dto.request.EmployeeStatusUpdateDTO;
import com.gm.hrms.dto.request.EmployeeUpdateDTO;
import com.gm.hrms.dto.response.EmployeeListResponseDTO;
import com.gm.hrms.dto.response.EmployeeResponseDTO;
import com.gm.hrms.enums.RecordStatus;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService service;

    // =========================================================================
    // GET ALL  —  paginated, filtered, sorted
    // =========================================================================
    /**
     * GET /api/employees
     *
     * Query params:
     *   page            (default 0)
     *   size            (default 10)
     *   search          free text (name/email/code)
     *   status          ACTIVE | INACTIVE | ON_HOLD
     *   employmentType  EMPLOYEE | INTERN | TRAINEE
     *   department      department name
     *   dateFrom        ISO date  e.g. 2024-01-01
     *   dateTo          ISO date  e.g. 2024-12-31
     *   sortBy          id | name | joiningDate   (default: id)
     *   sortDir         asc | desc                (default: asc)
     *   recordStatus    SUBMITTED | DRAFT         (default: SUBMITTED)
     */
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<EmployeeListResponseDTO>> getAll(
            @RequestParam(defaultValue = "0")           int    page,
            @RequestParam(defaultValue = "10")          int    size,
            @RequestParam(required = false)             String search,
            @RequestParam(required = false)             String status,
            @RequestParam(required = false)             String employmentType,
            @RequestParam(required = false)             String department,
            @RequestParam(required = false)             String dateFrom,
            @RequestParam(required = false)             String dateTo,
            @RequestParam(defaultValue = "id")          String sortBy,
            @RequestParam(defaultValue = "asc")         String sortDir,
            @RequestParam(required = false)             RecordStatus recordStatus
    ) {
        EmployeeListResponseDTO data = service.getAll(
                page, size, search, status, employmentType,
                department, dateFrom, dateTo, sortBy, sortDir, recordStatus);

        return ResponseEntity.ok(ApiResponse.<EmployeeListResponseDTO>builder()
                .success(true)
                .message("Employees fetched successfully")
                .data(data)
                .build());
    }

    // =========================================================================
    // GET BY ID
    // =========================================================================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/{id}")
    @Auditable(action = AuditAction.VIEW_EMPLOYEE, resource = "Employee", description = "View employee record")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> getById(@PathVariable Long id) {

        return ResponseEntity.ok(ApiResponse.<EmployeeResponseDTO>builder()
                .success(true)
                .message("Employee fetched successfully")
                .data(service.getById(id))
                .build());
    }

    // =========================================================================
    // UPDATE  (multipart)
    // =========================================================================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Auditable(action = AuditAction.UPDATE_EMPLOYEE, resource = "Employee", description = "Update employee record")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> update(
            @PathVariable Long id,
            @RequestParam("employee")                          String                      employeeJson,
            @RequestParam(required = false)                    MultipartFile               profileImage,
            @RequestParam(required = false)                    Map<String, MultipartFile>  documents,
            @RequestParam(required = false)                    Map<String, String>         reasons
    ) throws Exception {

        return ResponseEntity.ok(ApiResponse.<EmployeeResponseDTO>builder()
                .success(true)
                .message("Employee updated successfully")
                .data(service.update(id, employeeJson, profileImage, documents, reasons))
                .build());
    }

    // =========================================================================
    // STATUS UPDATE  —  HR + ADMIN
    // =========================================================================
    /**
     * PATCH /api/employees/{id}/status
     * Body: { "status": "ACTIVE" | "INACTIVE" | "ON_HOLD" }
     */
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeStatusUpdateDTO dto
    ) {
        service.updateStatus(id, dto);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Employee status updated successfully")
                .build());
    }

    // =========================================================================
    // DELETE  —  ADMIN ONLY
    // =========================================================================
    @PreAuthorize("hasRole('ADMIN')")     // ← HR cannot delete
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Employee deactivated successfully")
                .build());
    }
}