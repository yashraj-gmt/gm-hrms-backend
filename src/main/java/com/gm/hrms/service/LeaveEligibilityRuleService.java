package com.gm.hrms.service;

import com.gm.hrms.dto.request.LeaveEligibilityRuleRequestDTO;
import com.gm.hrms.dto.response.LeaveEligibilityRuleResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LeaveEligibilityRuleService {

    LeaveEligibilityRuleResponseDTO create(LeaveEligibilityRuleRequestDTO dto);

    LeaveEligibilityRuleResponseDTO getByPolicy(Long policyId);

    LeaveEligibilityRuleResponseDTO update(Long id, LeaveEligibilityRuleRequestDTO dto);

    PageResponseDTO<LeaveEligibilityRuleResponseDTO> getAll(Pageable pageable);

    void delete(Long id);
}