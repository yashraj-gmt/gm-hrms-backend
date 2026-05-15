package com.gm.hrms.repository;

import com.gm.hrms.entity.LeavePolicyLeaveType;
import com.gm.hrms.enums.AccrualType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeavePolicyLeaveTypeRepository extends JpaRepository<LeavePolicyLeaveType, Long> {

    // ================= BASIC =================
    boolean existsByLeavePolicyIdAndLeaveTypeIdAndIsActiveTrue(Long policyId, Long leaveTypeId);

    Optional<LeavePolicyLeaveType> findByIdAndIsActiveTrue(Long id);

    Optional<LeavePolicyLeaveType> findByLeavePolicyIdAndLeaveTypeIdAndIsActiveTrue(
            Long policyId, Long leaveTypeId
    );

    // ================= POLICY =================
    List<LeavePolicyLeaveType> findByLeavePolicyIdAndIsActiveTrue(Long policyId);

    // ================= ACCRUAL =================
    List<LeavePolicyLeaveType> findByAccrualTypeAndIsActiveTrue(AccrualType accrualType);

    // ================= PAGINATION =================
    Page<LeavePolicyLeaveType> findByIsActiveTrue(Pageable pageable);

    // ================= OPTIONAL =================
    List<LeavePolicyLeaveType> findByLeaveTypeId(Long leaveTypeId);
}