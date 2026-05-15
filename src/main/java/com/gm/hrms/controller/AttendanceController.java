package com.gm.hrms.controller;

import com.gm.hrms.audit.*;
import com.gm.hrms.audit.Auditable;
import com.gm.hrms.dto.request.AttendanceCorrectionRequestDTO;
import com.gm.hrms.dto.request.CorrectionRequestSubmitDTO;
import com.gm.hrms.dto.response.*;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService service;

    // ── Employee actions (no body — user resolved from JWT) ───────────────────

    @PostMapping("/check-in")
    @Auditable(action = AuditAction.ATTENDANCE_CHECK_IN, resource = "Attendance", description = "Employee check-in")
    public ResponseEntity<ApiResponse<AttendanceResponseDTO>> checkIn() {
        return ok("Check-in successful", service.checkIn());
    }

    @PostMapping("/check-out")
    @Auditable(action = AuditAction.ATTENDANCE_CHECK_OUT, resource = "Attendance", description = "Employee check-out")
    public ResponseEntity<ApiResponse<AttendanceResponseDTO>> checkOut() {
        return ok("Check-out successful", service.checkOut());
    }

    @PostMapping("/break-start")
    @Auditable(action = AuditAction.ATTENDANCE_BREAK_START, resource = "Attendance", description = "Break started")
    public ResponseEntity<ApiResponse<AttendanceResponseDTO>> breakStart() {
        return ok("Break started", service.breakStart());
    }

    @PostMapping("/break-end")
    @Auditable(action = AuditAction.ATTENDANCE_BREAK_END, resource = "Attendance", description = "Break ended")
    public ResponseEntity<ApiResponse<AttendanceResponseDTO>> breakEnd() {
        return ok("Break ended", service.breakEnd());
    }

    // ── My today ──────────────────────────────────────────────────────────────

    @GetMapping("/today/me")
    public ResponseEntity<ApiResponse<AttendanceResponseDTO>> getMyToday() {
        return ok("Today's attendance", service.getMyTodayAttendance());
    }

    // ── Admin: today for specific employee ────────────────────────────────────

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/today/{personalInformationId}")
    public ResponseEntity<ApiResponse<AttendanceResponseDTO>> getTodayById(
            @PathVariable Long personalInformationId) {
        return ok("Today's attendance", service.getTodayAttendance(personalInformationId));
    }

    // ── Admin: paginated list with filters ────────────────────────────────────

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<AttendanceResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String department) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "attendanceDate"));
        return ok("Attendance list fetched",
                service.getAllAttendance(pageable, date, status, department));
    }

    // ── Admin: daily summary stats ────────────────────────────────────────────

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<AttendanceSummaryDTO>> summary(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ok("Summary fetched",
                service.getDailySummary(date != null ? date : LocalDate.now()));
    }

    // ── Employee: own history ─────────────────────────────────────────────────

    @PreAuthorize("hasAnyRole('EMPLOYEE','INTERN','TRAINEE','ADMIN','HR')")
    @GetMapping("/my-history")
    public ResponseEntity<ApiResponse<PageResponseDTO<AttendanceResponseDTO>>> myHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String status) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "attendanceDate"));
        return ok("History fetched", service.getMyHistory(pageable, from, to, status));
    }

    // ── Admin: direct correction ──────────────────────────────────────────────

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping("/correct")
    @Auditable(action = AuditAction.CORRECT_ATTENDANCE, resource = "Attendance",
            description = "Admin attendance correction")
    public ResponseEntity<ApiResponse<AttendanceResponseDTO>> correct(
            @RequestBody AttendanceCorrectionRequestDTO dto) {
        return ok("Attendance corrected", service.correctAttendance(dto));
    }

    // ── Employee: submit correction request ───────────────────────────────────

    @PreAuthorize("hasAnyRole('EMPLOYEE','INTERN','TRAINEE')")
    @PostMapping("/correction-requests")
    public ResponseEntity<ApiResponse<AttendanceCorrectionResponseDTO>> submitRequest(
            @RequestBody CorrectionRequestSubmitDTO dto) {
        return ok("Correction request submitted", service.submitCorrectionRequest(dto));
    }

    // ── Admin: list correction requests ──────────────────────────────────────

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/correction-requests")
    public ResponseEntity<ApiResponse<PageResponseDTO<AttendanceCorrectionResponseDTO>>> listRequests(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return ok("Requests fetched",
                service.getCorrectionRequests(pageable, status));
    }

    // ── Admin: approve ────────────────────────────────────────────────────────

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/correction-requests/{id}/approve")
    @Auditable(action = AuditAction.CORRECT_ATTENDANCE, resource = "CorrectionRequest",
            description = "Approve correction request")
    public ResponseEntity<ApiResponse<AttendanceCorrectionResponseDTO>> approve(
            @PathVariable Long id) {
        return ok("Request approved", service.approveCorrectionRequest(id));
    }

    // ── Admin: reject ─────────────────────────────────────────────────────────

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PatchMapping("/correction-requests/{id}/reject")
    @Auditable(action = AuditAction.CORRECT_ATTENDANCE, resource = "CorrectionRequest",
            description = "Reject correction request")
    public ResponseEntity<ApiResponse<AttendanceCorrectionResponseDTO>> reject(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "") String remarks) {
        return ok("Request rejected", service.rejectCorrectionRequest(id, remarks));
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private <T> ResponseEntity<ApiResponse<T>> ok(String msg, T data) {
        return ResponseEntity.ok(ApiResponse.<T>builder()
                .success(true).message(msg).data(data).build());
    }
}