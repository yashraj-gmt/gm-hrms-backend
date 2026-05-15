package com.gm.hrms.service;

import com.gm.hrms.dto.request.SalaryGenerationRequestDTO;
import com.gm.hrms.dto.response.SalaryGenerationResponseDTO;

import java.util.List;

public interface SalaryGenerationService {
    SalaryGenerationResponseDTO generate(SalaryGenerationRequestDTO dto);
    SalaryGenerationResponseDTO finalizeSalary(Long generationId);
    SalaryGenerationResponseDTO getById(Long id);
    List<SalaryGenerationResponseDTO> getAll();
}