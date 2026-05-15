package com.gm.hrms.repository;

import com.gm.hrms.entity.LeaveEligibilityRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeaveEligibilityRuleRepository extends JpaRepository<LeaveEligibilityRule, Long> {

    Optional<LeaveEligibilityRule> findByLeavePolicyIdAndIsActiveTrue(Long policyId);

    boolean existsByLeavePolicyIdAndIsActiveTrue(Long policyId);

    Page<LeaveEligibilityRule> findByIsActiveTrue(Pageable pageable);
}
