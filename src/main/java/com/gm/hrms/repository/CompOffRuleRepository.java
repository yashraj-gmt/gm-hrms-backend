package com.gm.hrms.repository;

import com.gm.hrms.entity.CompOffRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompOffRuleRepository extends JpaRepository<CompOffRule, Long> {

    Optional<CompOffRule> findByLeavePolicyIdAndIsActiveTrue(Long policyId);

    boolean existsByLeavePolicyId(Long policyId);
}