package com.gm.hrms.repository;

import com.gm.hrms.entity.LeavePolicy;
import com.gm.hrms.enums.EmploymentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeavePolicyRepository extends JpaRepository<LeavePolicy, Long> {

    boolean existsByEmploymentTypeAndIsActiveTrue(EmploymentType type);

    Optional<LeavePolicy> findByIdAndIsActiveTrue(Long id);
    Optional<LeavePolicy> findByEmploymentTypeAndIsActiveTrue(EmploymentType  type);


}