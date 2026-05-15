package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.dto.request.TimesheetRequestDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.dto.response.TimesheetResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.TimesheetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/timesheets")
@RequiredArgsConstructor
public class TimesheetController {

    private final TimesheetService service;

    // ================= CREATE OR UPDATE =================
    @PostMapping
    @Auditable(
            action      = AuditAction.SAVE_TIMESHEET,
            resource    = "Timesheet",
            description = "Create or update timesheet entry"
    )
    public ResponseEntity<ApiResponse<?>> createOrUpdate(
            @RequestBody TimesheetRequestDTO request) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Timesheet saved")
                        .data(service.createOrUpdateTimesheet(request))
                        .build()
        );
    }

    // ================= SUBMIT =================
    @PostMapping("/{id}/submit")
    @Auditable(
            action      = AuditAction.SUBMIT_TIMESHEET,
            resource    = "Timesheet",
            description = "Employee submits timesheet for approval"
    )
    public ResponseEntity<ApiResponse<?>> submit(@PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Timesheet submitted")
                        .data(service.submitTimesheet(id))
                        .build()
        );
    }

    // ================= APPROVE =================
    @PostMapping("/{id}/approve")
    @Auditable(
            action      = AuditAction.APPROVE_TIMESHEET,
            resource    = "Timesheet",
            description = "Timesheet approved"
    )
    public ResponseEntity<ApiResponse<?>> approve(@PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Timesheet approved")
                        .data(service.approveTimesheet(id))
                        .build()
        );
    }

    // ================= REJECT =================
    @PostMapping("/{id}/reject")
    @Auditable(
            action      = AuditAction.REJECT_TIMESHEET,
            resource    = "Timesheet",
            description = "Timesheet rejected"
    )
    public ResponseEntity<ApiResponse<?>> reject(@PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Timesheet rejected")
                        .data(service.rejectTimesheet(id))
                        .build()
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Timesheet fetched")
                        .data(service.getTimesheetById(id))
                        .build()
        );
    }

    // ================= GET BY PERSON AND DATE =================
    @GetMapping("/person/{personId}/date/{date}")
    public ResponseEntity<ApiResponse<?>> getByPersonAndDate(
            @PathVariable Long personId,
            @PathVariable String date) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Timesheet fetched")
                        .data(service.getByPersonAndDate(personId, date))
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<TimesheetResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<TimesheetResponseDTO>>builder()
                        .success(true)
                        .message("Timesheets fetched successfully")
                        .data(service.getAllTimesheets(PageRequest.of(page, size)))
                        .build()
        );
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    @Auditable(
            action      = AuditAction.DELETE_TIMESHEET,
            resource    = "Timesheet",
            description = "Delete timesheet entry"
    )
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id) {

        service.deleteTimesheet(id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Timesheet deleted")
                        .build()
        );
    }

}