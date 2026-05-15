// src/main/java/com/gm/hrms/repository/AttendanceRepository.java
package com.gm.hrms.repository;

import com.gm.hrms.entity.Attendance;
import com.gm.hrms.enums.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByPersonalInformationIdAndAttendanceDate(
            Long personalInformationId, LocalDate attendanceDate);

    boolean existsByPersonalInformationIdAndAttendanceDate(
            Long personalInformationId, LocalDate attendanceDate);

    // ── Admin: list all by date (with optional status filter) ────────────────

    @Query("SELECT a FROM Attendance a " +
            "LEFT JOIN FETCH a.personalInformation pi " +
            "LEFT JOIN FETCH pi.workProfile wp " +
            "WHERE a.attendanceDate = :date")
    Page<Attendance> findByAttendanceDateWithProfile(
            @Param("date") LocalDate date, Pageable pageable);

    @Query("SELECT a FROM Attendance a " +
            "LEFT JOIN FETCH a.personalInformation pi " +
            "LEFT JOIN FETCH pi.workProfile wp " +
            "WHERE a.attendanceDate = :date AND a.status = :status")
    Page<Attendance> findByAttendanceDateAndStatusWithProfile(
            @Param("date") LocalDate date,
            @Param("status") AttendanceStatus status,
            Pageable pageable);

    // Summary (no pagination — only fetches status column)
    @Query("SELECT a.status, COUNT(a) FROM Attendance a " +
            "WHERE a.attendanceDate = :date GROUP BY a.status")
    List<Object[]> countByStatusForDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(a) FROM Attendance a " +
            "JOIN a.calculation calc " +
            "WHERE a.attendanceDate = :date AND calc.lateMinutes > 0")
    long countLateArrivalsForDate(@Param("date") LocalDate date);

    // ── Employee: own history ─────────────────────────────────────────────────

    Page<Attendance> findByPersonalInformationIdAndAttendanceDateBetween(
            Long personalInformationId,
            LocalDate from,
            LocalDate to,
            Pageable pageable);

    @Query("SELECT a FROM Attendance a " +
            "WHERE a.personalInformation.id = :personId " +
            "AND a.attendanceDate BETWEEN :from AND :to " +
            "AND a.status = :status")
    Page<Attendance> findByPersonalInformationIdAndDateBetweenAndStatus(
            @Param("personId") Long personId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("status") AttendanceStatus status,
            Pageable pageable);

    // ── Bulk fetch with calculation (used by getAll) ──────────────────────────

    @Query("SELECT a FROM Attendance a LEFT JOIN FETCH a.calculation WHERE a.id IN :ids")
    List<Attendance> findAllWithCalculationByIds(@Param("ids") List<Long> ids);

    // ── Date range (non-paginated, for summaries) ─────────────────────────────

    List<Attendance> findByPersonalInformationIdAndAttendanceDateBetween(
            Long personalInformationId, LocalDate from, LocalDate to);
}