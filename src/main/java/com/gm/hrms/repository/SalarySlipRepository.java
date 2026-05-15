package com.gm.hrms.repository;

import com.gm.hrms.entity.SalarySlip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SalarySlipRepository extends JpaRepository<SalarySlip, Long> {

    boolean existsByPersonalInformationIdAndMonthAndYear(
            Long personalId, Integer month, Integer year);

    Optional<SalarySlip> findByPersonalInformationIdAndMonthAndYear(
            Long personalId, Integer month, Integer year);

    List<SalarySlip> findBySalaryGenerationId(Long generationId);

    List<SalarySlip> findByPersonalInformationIdOrderByYearDescMonthDesc(Long personalId);
}