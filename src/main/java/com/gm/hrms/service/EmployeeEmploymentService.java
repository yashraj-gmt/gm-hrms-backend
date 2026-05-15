package com.gm.hrms.service;

import com.gm.hrms.dto.request.EmployeeEmploymentRequestDTO;
import com.gm.hrms.dto.response.EmployeeEmploymentResponseDTO;
import com.gm.hrms.entity.Employee;

public interface EmployeeEmploymentService {

    void saveOrUpdate(Employee employee,
                      EmployeeEmploymentRequestDTO dto);

    EmployeeEmploymentResponseDTO getByEmployee(Employee employee);
}