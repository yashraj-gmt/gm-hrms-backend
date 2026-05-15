package com.gm.hrms.repository;

import com.gm.hrms.entity.AttendanceLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceLogRepository extends JpaRepository<AttendanceLog, Long> {
}