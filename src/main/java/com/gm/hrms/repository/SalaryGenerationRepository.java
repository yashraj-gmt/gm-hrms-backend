package com.gm.hrms.repository;

import com.gm.hrms.entity.SalaryGeneration;
import com.gm.hrms.enums.SalaryGenerationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SalaryGenerationRepository extends JpaRepository<SalaryGeneration, Long> {

    boolean existsByMonthAndYear(Integer month, Integer year);

    Optional<SalaryGeneration> findByMonthAndYear(Integer month, Integer year);

    List<SalaryGeneration> findAllByOrderByYearDescMonthDesc();

    List<SalaryGeneration> findByStatus(SalaryGenerationStatus status);
}