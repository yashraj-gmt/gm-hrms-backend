package com.gm.hrms.dto.response;

import com.gm.hrms.enums.AttendanceStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class AttendanceResponseDTO {

    private Long id;
    private Long personalInformationId;

    // ── Employee info ─────────────────────────────────────────────────────────
    private String employeeCode;
    private String employeeName;
    private String designation;
    private String department;
    private String shift;

    // ── Attendance data ───────────────────────────────────────────────────────
    private LocalDate attendanceDate;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Integer workMinutes;
    private Integer breakMinutes;
    private Integer lateMinutes;
    private Integer overtimeMinutes;
    private AttendanceStatus status;

    // ── Real-time state flags (employee view) ─────────────────────────────────
    private Boolean isCheckedIn;
    private Boolean isOnBreak;
    private Boolean isCheckedOut;

    // ── Break detail ──────────────────────────────────────────────────────────
    private List<BreakLogDTO> breakLogs;

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class BreakLogDTO {
        private Long id;
        private LocalDateTime breakStart;
        private LocalDateTime breakEnd;
        private Integer durationMinutes;
    }
}