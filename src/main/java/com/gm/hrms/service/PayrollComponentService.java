package com.gm.hrms.service;

import com.gm.hrms.dto.request.PayrollComponentRequestDTO;
import com.gm.hrms.dto.response.PayrollComponentResponseDTO;

import java.util.List;

public interface PayrollComponentService {
    PayrollComponentResponseDTO create(PayrollComponentRequestDTO dto);
    PayrollComponentResponseDTO update(Long id, PayrollComponentRequestDTO dto);
    PayrollComponentResponseDTO getById(Long id);
    List<PayrollComponentResponseDTO> getAll();
    void delete(Long id);
}