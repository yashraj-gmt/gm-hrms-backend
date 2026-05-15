package com.gm.hrms.repository;

import com.gm.hrms.entity.AttendanceCalculation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AttendanceCalculationRepository
        extends JpaRepository<AttendanceCalculation,Long> {

    Optional<AttendanceCalculation> findByAttendanceId(Long attendanceId);

    @Query("""
SELECT COUNT(a)
FROM AttendanceCalculation a
WHERE a.attendance.personalInformation.id = :personId
AND a.lateMinutes > 0
AND MONTH(a.attendance.attendanceDate) = :month
AND YEAR(a.attendance.attendanceDate) = :year
""")
    int countMonthlyLate(Long personId, int month, int year);
}