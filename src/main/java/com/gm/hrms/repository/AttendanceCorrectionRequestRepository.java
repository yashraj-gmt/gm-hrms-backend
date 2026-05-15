// src/main/java/com/gm/hrms/repository/AttendanceCorrectionRequestRepository.java
package com.gm.hrms.repository;

import com.gm.hrms.entity.AttendanceCorrectionRequest;
import com.gm.hrms.enums.CorrectionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AttendanceCorrectionRequestRepository
        extends JpaRepository<AttendanceCorrectionRequest, Long> {

    Page<AttendanceCorrectionRequest> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<AttendanceCorrectionRequest> findByStatusOrderByCreatedAtDesc(
            CorrectionStatus status, Pageable pageable);

    @Query("SELECT r FROM AttendanceCorrectionRequest r " +
            "WHERE r.personalInformation.id = :personId " +
            "ORDER BY r.createdAt DESC")
    Page<AttendanceCorrectionRequest> findByPersonalInformationId(
            Long personId, Pageable pageable);

    boolean existsByAttendanceIdAndStatusIn(Long attendanceId,
                                            java.util.List<CorrectionStatus> statuses);
}