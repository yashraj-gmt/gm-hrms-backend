package com.gm.hrms.repository;

import com.gm.hrms.entity.EmployeeSalaryStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeSalaryStructureRepository
        extends JpaRepository<EmployeeSalaryStructure, Long> {

    @Query("""
        SELECT s FROM EmployeeSalaryStructure s
        WHERE s.personalInformation.id = :personalId
          AND s.isActive = true
          AND s.effectiveFrom <= :asOfDate
          AND (s.effectiveTo IS NULL OR s.effectiveTo >= :asOfDate)
        ORDER BY s.effectiveFrom DESC
        """)
    Optional<EmployeeSalaryStructure> findActiveStructure(
            @Param("personalId") Long personalId,
            @Param("asOfDate") LocalDate asOfDate
    );

    @Query("""
        SELECT s FROM EmployeeSalaryStructure s
        WHERE s.isActive = true
          AND s.effectiveFrom <= :asOfDate
          AND (s.effectiveTo IS NULL OR s.effectiveTo >= :asOfDate)
        """)
    List<EmployeeSalaryStructure> findAllActiveStructures(
            @Param("asOfDate") LocalDate asOfDate
    );

    List<EmployeeSalaryStructure> findByPersonalInformationIdOrderByEffectiveFromDesc(Long personalId);

    boolean existsByPersonalInformationIdAndEffectiveFrom(Long personalId, LocalDate effectiveFrom);
}