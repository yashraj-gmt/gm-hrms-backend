package com.gm.hrms.service;

import com.gm.hrms.dto.request.DepartmentRequestDTO;
import com.gm.hrms.dto.response.DepartmentResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import org.springframework.data.domain.Pageable;

public interface DepartmentService {

    DepartmentResponseDTO createDepartment(DepartmentRequestDTO dto);

    DepartmentResponseDTO updateDepartment(Long id, DepartmentRequestDTO dto);

    DepartmentResponseDTO getDepartmentById(Long id);

    PageResponseDTO<DepartmentResponseDTO> getAllDepartments(
            String search, Boolean status, Pageable pageable
    );

    void deleteDepartment(Long id);
}