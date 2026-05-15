package com.gm.hrms.repository;

import com.gm.hrms.entity.LeaveEncashmentRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeaveEncashmentRuleRepository extends JpaRepository<LeaveEncashmentRule, Long> {

    Optional<LeaveEncashmentRule> findByLeavePolicyIdAndIsActiveTrue(Long policyId);

    boolean existsByLeavePolicyId(Long policyId);

    Page<LeaveEncashmentRule> findByIsActiveTrue(Pageable pageable);
}