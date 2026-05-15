package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.CompOffRuleRequestDTO;
import com.gm.hrms.dto.response.CompOffRuleResponseDTO;
import com.gm.hrms.entity.CompOffRule;
import com.gm.hrms.entity.LeavePolicy;

public class CompOffRuleMapper {

    public static CompOffRule toEntity(CompOffRuleRequestDTO dto, LeavePolicy policy) {
        return CompOffRule.builder()
                .leavePolicy(policy)
                .isEnabled(dto.getIsEnabled())
                .approvalRequired(dto.getApprovalRequired())
                .maxPerMonth(dto.getMaxPerMonth())
                .expiryDays(dto.getExpiryDays())
                .isActive(true)
                .isSystemDefined(false)
                .build();
    }

    public static void updateEntity(CompOffRule entity, CompOffRuleRequestDTO dto) {

        if (dto.getIsEnabled() != null)
            entity.setIsEnabled(dto.getIsEnabled());

        if (dto.getApprovalRequired() != null)
            entity.setApprovalRequired(dto.getApprovalRequired());

        if (dto.getMaxPerMonth() != null)
            entity.setMaxPerMonth(dto.getMaxPerMonth());

        if (dto.getExpiryDays() != null)
            entity.setExpiryDays(dto.getExpiryDays());
    }

    public static CompOffRuleResponseDTO toResponse(CompOffRule entity) {
        return CompOffRuleResponseDTO.builder()
                .id(entity.getId())
                .policyId(entity.getLeavePolicy().getId())
                .isEnabled(entity.getIsEnabled())
                .approvalRequired(entity.getApprovalRequired())
                .maxPerMonth(entity.getMaxPerMonth())
                .expiryDays(entity.getExpiryDays())
                .build();
    }
}