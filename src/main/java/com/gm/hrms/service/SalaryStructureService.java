package com.gm.hrms.service;

import com.gm.hrms.dto.request.SalaryStructureRequestDTO;
import com.gm.hrms.dto.response.SalaryStructureResponseDTO;

import java.util.List;

public interface SalaryStructureService {
    SalaryStructureResponseDTO create(Long personalId, SalaryStructureRequestDTO dto);
    SalaryStructureResponseDTO update(Long structureId, SalaryStructureRequestDTO dto);
    SalaryStructureResponseDTO getActive(Long personalId);
    List<SalaryStructureResponseDTO> getHistory(Long personalId);
}