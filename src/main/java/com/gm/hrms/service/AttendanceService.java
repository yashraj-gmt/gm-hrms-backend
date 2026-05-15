// src/main/java/com/gm/hrms/service/AttendanceService.java
package com.gm.hrms.service;

import com.gm.hrms.dto.request.AttendanceCorrectionRequestDTO;
import com.gm.hrms.dto.request.CorrectionRequestSubmitDTO;
import com.gm.hrms.dto.response.*;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface AttendanceService {

    // ── Employee actions (JWT-resolved) ───────────────────────────────────────
    AttendanceResponseDTO checkIn();
    AttendanceResponseDTO checkOut();
    AttendanceResponseDTO breakStart();
    AttendanceResponseDTO breakEnd();

    // ── Queries ───────────────────────────────────────────────────────────────
    AttendanceResponseDTO getMyTodayAttendance();
    AttendanceResponseDTO getTodayAttendance(Long personalInformationId);

    PageResponseDTO<AttendanceResponseDTO> getAllAttendance(
            Pageable pageable, LocalDate date, String status, String department);

    PageResponseDTO<AttendanceResponseDTO> getMyHistory(
            Pageable pageable, LocalDate from, LocalDate to, String status);

    AttendanceSummaryDTO getDailySummary(LocalDate date);

    // ── Admin correction ──────────────────────────────────────────────────────
    AttendanceResponseDTO correctAttendance(AttendanceCorrectionRequestDTO dto);

    // ── Correction request workflow ───────────────────────────────────────────
    AttendanceCorrectionResponseDTO submitCorrectionRequest(CorrectionRequestSubmitDTO dto);
    PageResponseDTO<AttendanceCorrectionResponseDTO> getCorrectionRequests(
            Pageable pageable, String status);
    AttendanceCorrectionResponseDTO approveCorrectionRequest(Long id);
    AttendanceCorrectionResponseDTO rejectCorrectionRequest(Long id, String remarks);
}