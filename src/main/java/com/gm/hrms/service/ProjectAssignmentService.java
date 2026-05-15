package com.gm.hrms.service;

import com.gm.hrms.dto.request.ProjectAssignmentRequestDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.dto.response.ProjectAssignmentResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProjectAssignmentService {

    ProjectAssignmentResponseDTO assign(ProjectAssignmentRequestDTO dto);

    void remove(Long projectId, Long employeeId);

    PageResponseDTO<ProjectAssignmentResponseDTO> getEmployeesByProject(Long projectId, Pageable pageable);

    PageResponseDTO<ProjectAssignmentResponseDTO> getProjectsByEmployee(Long employeeId, Pageable pageable);
}

