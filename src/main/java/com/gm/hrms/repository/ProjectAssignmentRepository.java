package com.gm.hrms.repository;

import com.gm.hrms.entity.ProjectAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectAssignmentRepository extends JpaRepository<ProjectAssignment, Long> {

    Page<ProjectAssignment> findByProjectId(Long projectId, Pageable pageable);

    Page<ProjectAssignment> findByEmployeeId(Long employeeId, Pageable pageable);

    boolean existsByProjectIdAndEmployeeId(Long projectId, Long employeeId);

    void deleteByProjectIdAndEmployeeId(Long projectId, Long employeeId);
}

