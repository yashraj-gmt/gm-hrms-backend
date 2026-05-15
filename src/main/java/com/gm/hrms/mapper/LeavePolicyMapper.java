package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.LeavePolicyRequestDTO;
import com.gm.hrms.dto.response.LeavePolicyResponseDTO;
import com.gm.hrms.entity.LeavePolicy;

public class LeavePolicyMapper {

    private LeavePolicyMapper() {}

    // CREATE
    public static LeavePolicy toEntity(LeavePolicyRequestDTO dto) {
        return LeavePolicy.builder()
                .policyName(dto.getPolicyName())
                .description(dto.getDescription())
                .employmentType(dto.getEmploymentType())
                .effectiveFrom(dto.getEffectiveFrom())
                .effectiveTo(dto.getEffectiveTo())
                .requiresApproval(dto.getRequiresApproval())
                .allowHalfDay(dto.getAllowHalfDay())
                .allowBackdatedLeave(dto.getAllowBackdatedLeave())
                .sandwichRuleEnabled(dto.getSandwichRuleEnabled())
                .isActive(true)
                .isSystemDefined(false)
                .build();
    }

    // RESPONSE
    public static LeavePolicyResponseDTO toResponse(LeavePolicy entity) {
        return LeavePolicyResponseDTO.builder()
                .id(entity.getId())
                .policyName(entity.getPolicyName())
                .description(entity.getDescription())
                .employmentType(entity.getEmploymentType())
                .effectiveFrom(entity.getEffectiveFrom())
                .effectiveTo(entity.getEffectiveTo())
                .requiresApproval(entity.getRequiresApproval())
                .allowHalfDay(entity.getAllowHalfDay())
                .allowBackdatedLeave(entity.getAllowBackdatedLeave())
                .sandwichRuleEnabled(entity.getSandwichRuleEnabled())
                .isActive(entity.getIsActive())
                .isSystemDefined(entity.getIsSystemDefined())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // PATCH UPDATE
    public static void updateEntity(LeavePolicy entity, LeavePolicyRequestDTO dto) {

        if (dto.getPolicyName() != null) entity.setPolicyName(dto.getPolicyName());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getEmploymentType() != null) entity.setEmploymentType(dto.getEmploymentType());

        if (dto.getEffectiveFrom() != null) entity.setEffectiveFrom(dto.getEffectiveFrom());
        if (dto.getEffectiveTo() != null) entity.setEffectiveTo(dto.getEffectiveTo());

        if (dto.getRequiresApproval() != null) entity.setRequiresApproval(dto.getRequiresApproval());
        if (dto.getAllowHalfDay() != null) entity.setAllowHalfDay(dto.getAllowHalfDay());
        if (dto.getAllowBackdatedLeave() != null) entity.setAllowBackdatedLeave(dto.getAllowBackdatedLeave());
        if (dto.getSandwichRuleEnabled() != null) entity.setSandwichRuleEnabled(dto.getSandwichRuleEnabled());
    }
}