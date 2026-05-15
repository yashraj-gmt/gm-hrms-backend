package com.gm.hrms.repository;

import com.gm.hrms.entity.AttendanceBreakLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceBreakLogRepository
        extends JpaRepository<AttendanceBreakLog,Long> {

    List<AttendanceBreakLog> findByAttendanceId(Long attendanceId);

    AttendanceBreakLog findTopByAttendanceIdOrderByBreakStartDesc(Long attendanceId);


}