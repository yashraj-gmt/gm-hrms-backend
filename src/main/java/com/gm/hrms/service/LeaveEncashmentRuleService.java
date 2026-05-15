package com.gm.hrms.service;

import com.gm.hrms.dto.request.LeaveEncashmentRuleRequestDTO;
import com.gm.hrms.dto.response.LeaveEncashmentRuleResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import org.springframework.data.domain.Pageable;

public interface LeaveEncashmentRuleService {

    LeaveEncashmentRuleResponseDTO create(LeaveEncashmentRuleRequestDTO dto);

    LeaveEncashmentRuleResponseDTO getByPolicy(Long policyId);

    LeaveEncashmentRuleResponseDTO patchUpdate(Long id, LeaveEncashmentRuleRequestDTO dto);

    void delete(Long id);

    PageResponseDTO<LeaveEncashmentRuleResponseDTO> getAll(Pageable pageable);
}