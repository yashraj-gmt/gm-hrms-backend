package com.gm.hrms.repository;

import com.gm.hrms.entity.SubDepartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubDepartmentRepository extends JpaRepository<SubDepartment, Long> {

    List<SubDepartment> findByDepartmentId(Long departmentId);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);
}