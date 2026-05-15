package com.gm.hrms.repository;

import com.gm.hrms.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {

    boolean existsByCode(String code);

    Optional<LeaveType> findByIdAndIsActiveTrue(Long id);

    List<LeaveType> findByIsActiveTrue();

    // 🔥 NEW (for CompOff validation)
    boolean existsByIsCompOffTrueAndIsActiveTrue();
}