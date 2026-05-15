package com.gm.hrms.repository;

import com.gm.hrms.entity.PayrollComponent;
import com.gm.hrms.enums.PayrollComponentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayrollComponentRepository extends JpaRepository<PayrollComponent, Long> {

    boolean existsByCode(String code);

    boolean existsByName(String name);

    List<PayrollComponent> findAllByIsActiveTrueOrderByDisplayOrderAsc();

    List<PayrollComponent> findAllByTypeAndIsActiveTrue(PayrollComponentType type);
}