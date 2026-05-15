package com.gm.hrms.repository;

import com.gm.hrms.entity.ShiftAssignment;
import com.gm.hrms.enums.AssignmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftAssignmentRepository extends JpaRepository<ShiftAssignment, Long> {

    // ── FIXED: findFirst avoids NonUniqueResultException ─────────────────────
    // Returns the most recently created active/upcoming assignment for a person.
    Optional<ShiftAssignment> findFirstByPersonalInformationIdAndStatusAndIsActive(
            Long personalInformationId,
            AssignmentStatus status,
            Boolean isActive
    );

    // For history — all assignments ordered newest first
    Page<ShiftAssignment> findAllByPersonalInformationIdAndIsActiveTrueOrderByEffectiveFromDesc(
            Long personalInformationId,
            Pageable pageable
    );

    List<ShiftAssignment> findAllByPersonalInformationIdAndIsActiveTrueOrderByEffectiveFromDesc(
            Long personalInformationId
    );

    // For admin/HR — all active assignments
    @Query("SELECT sa FROM ShiftAssignment sa WHERE sa.isActive = true ORDER BY sa.createdAt DESC")
    Page<ShiftAssignment> findAllActive(Pageable pageable);

    // Current shift name lookup (used in searchEligiblePersons — single query per person)
    @Query("SELECT s.shiftName FROM ShiftAssignment sa " +
            "JOIN sa.shift s " +
            "WHERE sa.personalInformation.id = :piId " +
            "AND sa.status = 'ACTIVE' AND sa.isActive = true " +
            "ORDER BY sa.createdAt DESC")
    List<String> findActiveShiftNameByPersonalInformationId(@Param("piId") Long piId);
}