package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.LeaveEncashmentRuleRequestDTO;
import com.gm.hrms.dto.response.LeaveEncashmentRuleResponseDTO;
import com.gm.hrms.entity.LeaveEncashmentRule;
import com.gm.hrms.entity.LeavePolicy;

public class LeaveEncashmentRuleMapper {

    public static LeaveEncashmentRule toEntity(
            LeaveEncashmentRuleRequestDTO dto,
            LeavePolicy policy
    ) {
        return LeaveEncashmentRule.builder()
                .leavePolicy(policy)
                .isEnabled(dto.getIsEnabled())
                .maxEncashment(dto.getMaxEncashment())
                .timing(dto.getTiming())
                .isActive(true)
                .isSystemDefined(false)
                .build();
    }

    public static void updateEntity(
            LeaveEncashmentRule entity,
            LeaveEncashmentRuleRequestDTO dto
    ) {

        if (dto.getIsEnabled() != null)
            entity.setIsEnabled(dto.getIsEnabled());

        if (dto.getMaxEncashment() != null)
            entity.setMaxEncashment(dto.getMaxEncashment());

        if (dto.getTiming() != null)
            entity.setTiming(dto.getTiming());
    }

    public static LeaveEncashmentRuleResponseDTO toResponse(LeaveEncashmentRule entity) {

        return LeaveEncashmentRuleResponseDTO.builder()
                .id(entity.getId())
                .policyId(entity.getLeavePolicy().getId())
                .isEnabled(entity.getIsEnabled())
                .maxEncashment(entity.getMaxEncashment())
                .timing(entity.getTiming())
                .build();
    }
}