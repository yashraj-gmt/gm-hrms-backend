package com.gm.hrms.repository;

import com.gm.hrms.entity.LeaveRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    Page<LeaveRequest> findByPersonalId(Long personalId, Pageable pageable);
    boolean existsByPersonalIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long personalId,
            LocalDate end,
            LocalDate start
    );

    Page<LeaveRequest> findByPersonalIdOrderByCreatedAtDesc(Long personalId, Pageable pageable);

    Page<LeaveRequest> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("""
        SELECT COUNT(lr)
        FROM LeaveRequest lr
        JOIN lr.leaveType lt
        WHERE lr.personalId = :personalId
          AND lr.status    = 'APPROVED'
          AND lt.isPaid    = true
          AND lr.startDate >= :from
          AND lr.endDate   <= :to
          AND (lr.isCancelled IS NULL OR lr.isCancelled = false)
        """)
    long countApprovedPaidLeaves(
            @Param("personalId") Long personalId,
            @Param("from")       LocalDate from,
            @Param("to")         LocalDate to
    );

    @Query("""
        SELECT COALESCE(SUM(lr.totalDays), 0.0)
        FROM LeaveRequest lr
        JOIN lr.leaveType lt
        WHERE lr.personalId = :personalId
          AND lr.status    = 'APPROVED'
          AND lt.isPaid    = false
          AND lr.startDate >= :from
          AND lr.endDate   <= :to
          AND (lr.isCancelled IS NULL OR lr.isCancelled = false)
        """)
    double sumUnpaidApprovedLeaveDays(
            @Param("personalId") Long personalId,
            @Param("from")       LocalDate from,
            @Param("to")         LocalDate to
    );
}