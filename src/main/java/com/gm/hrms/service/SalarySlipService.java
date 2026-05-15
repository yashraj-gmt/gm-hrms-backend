package com.gm.hrms.service;

import com.gm.hrms.dto.response.SalarySlipResponseDTO;

import java.util.List;

public interface SalarySlipService {
    SalarySlipResponseDTO getById(Long slipId);
    SalarySlipResponseDTO getByPersonAndMonth(Long personalId, Integer month, Integer year);
    List<SalarySlipResponseDTO> getByEmployee(Long personalId);
    byte[] downloadPdf(Long slipId);
}