package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.CarryForwardRuleRequestDTO;
import com.gm.hrms.dto.response.CarryForwardRuleResponseDTO;
import com.gm.hrms.entity.CarryForwardRule;
import com.gm.hrms.entity.LeavePolicy;

public class CarryForwardRuleMapper {

    public static CarryForwardRule toEntity(CarryForwardRuleRequestDTO dto, LeavePolicy policy) {
        return CarryForwardRule.builder()
                .leavePolicy(policy)
                .isEnabled(dto.getIsEnabled())
                .maxCarryForward(dto.getMaxCarryForward())
                .expiryDays(dto.getExpiryDays())
                .isActive(true)
                .isSystemDefined(false)
                .build();
    }

    public static void updateEntity(CarryForwardRule entity, CarryForwardRuleRequestDTO dto) {

        if (dto.getIsEnabled() != null)
            entity.setIsEnabled(dto.getIsEnabled());

        if (dto.getMaxCarryForward() != null)
            entity.setMaxCarryForward(dto.getMaxCarryForward());

        if (dto.getExpiryDays() != null)
            entity.setExpiryDays(dto.getExpiryDays());
    }

    public static CarryForwardRuleResponseDTO toResponse(CarryForwardRule entity) {
        return CarryForwardRuleResponseDTO.builder()
                .id(entity.getId())
                .policyId(entity.getLeavePolicy().getId())
                .isEnabled(entity.getIsEnabled())
                .maxCarryForward(entity.getMaxCarryForward())
                .expiryDays(entity.getExpiryDays())
                .build();
    }
}