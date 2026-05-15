package com.gm.hrms.repository;

import com.gm.hrms.entity.Employee;
import com.gm.hrms.enums.EmploymentType;
import com.gm.hrms.enums.RecordStatus;
import com.gm.hrms.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeeRepository
        extends JpaRepository<Employee, Long>,
        JpaSpecificationExecutor<Employee> {

    Optional<Employee> findByPersonalInformationId(Long personalInformationId);

    boolean existsByEmployeeCode(String employeeCode);

    boolean existsByEmployeeCodeAndIdNot(String employeeCode, Long id);

    // ── Summary counts ────────────────────────────────────────────────────────

    /**
     * Total SUBMITTED employees (all employment types, active persons).
     */
    @Query("""
        SELECT COUNT(e) FROM Employee e
        JOIN e.personalInformation pi
        WHERE pi.recordStatus = com.gm.hrms.enums.RecordStatus.SUBMITTED
          AND pi.active = true
    """)
    long countAllSubmitted();

    /**
     * Count SUBMITTED employees by work-profile status.
     */
    @Query("""
        SELECT COUNT(e) FROM Employee e
        JOIN e.personalInformation pi
        LEFT JOIN pi.workProfile wp
        WHERE pi.recordStatus = com.gm.hrms.enums.RecordStatus.SUBMITTED
          AND pi.active = true
          AND wp.status = :status
    """)
    long countSubmittedByStatus(@Param("status") Status status);

    /**
     * Count SUBMITTED employees by employment type.
     */
    @Query("""
        SELECT COUNT(e) FROM Employee e
        JOIN e.personalInformation pi
        WHERE pi.recordStatus = com.gm.hrms.enums.RecordStatus.SUBMITTED
          AND pi.active = true
          AND pi.employmentType = :type
    """)
    long countSubmittedByType(@Param("type") EmploymentType type);

    /**
     * Count DRAFT employees.
     */
    @Query("""
        SELECT COUNT(e) FROM Employee e
        JOIN e.personalInformation pi
        WHERE pi.recordStatus = com.gm.hrms.enums.RecordStatus.DRAFT
          AND pi.active = true
    """)
    long countDrafts();
}