package com.gm.hrms.repository;

import com.gm.hrms.entity.LeaveApplicationRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeaveApplicationRuleRepository extends JpaRepository<LeaveApplicationRule, Long> {

    // ================= BASIC =================

    Optional<LeaveApplicationRule> findByIdAndIsActiveTrue(Long id);

    Page<LeaveApplicationRule> findByIsActiveTrue(Pageable pageable);
    // ================= POLICY BASED =================

    Optional<LeaveApplicationRule> findByLeavePolicyIdAndIsActiveTrue(Long policyId);

    boolean existsByLeavePolicyIdAndIsActiveTrue(Long policyId);

    // ================= OPTIONAL (FUTURE USE) =================

    List<LeaveApplicationRule> findByLeavePolicyId(Long policyId);
}