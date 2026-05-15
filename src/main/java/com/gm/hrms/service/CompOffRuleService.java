package com.gm.hrms.service;

import com.gm.hrms.dto.request.CompOffRuleRequestDTO;
import com.gm.hrms.dto.response.CompOffRuleResponseDTO;

public interface CompOffRuleService {

    CompOffRuleResponseDTO create(CompOffRuleRequestDTO dto);

    CompOffRuleResponseDTO getByPolicy(Long policyId);

    CompOffRuleResponseDTO update(Long id, CompOffRuleRequestDTO dto);

    CompOffRuleResponseDTO patchUpdate(Long id, CompOffRuleRequestDTO dto);

    void delete(Long id);
}