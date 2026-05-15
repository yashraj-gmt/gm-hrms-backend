package com.gm.hrms.repository;

import com.gm.hrms.entity.Employee;
import com.gm.hrms.entity.EmployeeEmployment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeEmploymentRepository
        extends JpaRepository<EmployeeEmployment, Long> {

    Optional<EmployeeEmployment> findByEmployee(Employee employee);
}
