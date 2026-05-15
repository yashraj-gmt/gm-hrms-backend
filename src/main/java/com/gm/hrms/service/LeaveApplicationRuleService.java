package com.gm.hrms.service;

import com.gm.hrms.dto.request.LeaveApplicationRuleRequestDTO;
import com.gm.hrms.dto.response.LeaveApplicationRuleResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LeaveApplicationRuleService {

    LeaveApplicationRuleResponseDTO create(LeaveApplicationRuleRequestDTO dto);

    LeaveApplicationRuleResponseDTO getByPolicy(Long policyId);

    PageResponseDTO<LeaveApplicationRuleResponseDTO> getAll(Pageable pageable);

    LeaveApplicationRuleResponseDTO patchUpdate(Long id, LeaveApplicationRuleRequestDTO dto);

    void delete(Long id);
}