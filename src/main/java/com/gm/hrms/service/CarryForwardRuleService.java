package com.gm.hrms.service;

import com.gm.hrms.dto.request.CarryForwardRuleRequestDTO;
import com.gm.hrms.dto.response.CarryForwardRuleResponseDTO;

public interface CarryForwardRuleService {

    CarryForwardRuleResponseDTO create(CarryForwardRuleRequestDTO dto);

    CarryForwardRuleResponseDTO getByPolicy(Long policyId);

    CarryForwardRuleResponseDTO patchUpdate(Long id, CarryForwardRuleRequestDTO dto);

    void delete(Long id);
}