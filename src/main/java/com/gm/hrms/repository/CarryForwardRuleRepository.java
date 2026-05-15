package com.gm.hrms.repository;

import com.gm.hrms.entity.CarryForwardRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarryForwardRuleRepository extends JpaRepository<CarryForwardRule, Long> {

    Optional<CarryForwardRule> findByLeavePolicyIdAndIsActiveTrue(Long policyId);

    boolean existsByLeavePolicyId(Long policyId);
}