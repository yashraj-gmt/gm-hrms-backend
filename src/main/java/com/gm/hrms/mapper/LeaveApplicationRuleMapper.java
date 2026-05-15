package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.LeaveApplicationRuleRequestDTO;
import com.gm.hrms.dto.response.LeaveApplicationRuleResponseDTO;
import com.gm.hrms.entity.LeaveApplicationRule;
import com.gm.hrms.entity.LeavePolicy;

public class LeaveApplicationRuleMapper {

    private LeaveApplicationRuleMapper() {}

    // CREATE
    public static LeaveApplicationRule toEntity(
            LeaveApplicationRuleRequestDTO dto,
            LeavePolicy policy
    ) {
        return LeaveApplicationRule.builder()
                .leavePolicy(policy)
                .allowHalfDay(dto.getAllowHalfDay())
                .minLeaveDuration(dto.getMinLeaveDuration())
                .maxConsecutiveDays(dto.getMaxConsecutiveDays())
                .applyBeforeDays(dto.getApplyBeforeDays())
                .allowBackdatedLeave(dto.getAllowBackdatedLeave())
                .sandwichRuleEnabled(dto.getSandwichRuleEnabled())
                .includeHolidays(dto.getIncludeHolidays())
                .includeWeekends(dto.getIncludeWeekends())
                .isActive(true)
                .isSystemDefined(false)
                .build();
    }

    // PATCH
    public static void patchUpdate(LeaveApplicationRule entity, LeaveApplicationRuleRequestDTO dto) {

        if (dto.getAllowHalfDay() != null)
            entity.setAllowHalfDay(dto.getAllowHalfDay());

        if (dto.getMinLeaveDuration() != null)
            entity.setMinLeaveDuration(dto.getMinLeaveDuration());

        if (dto.getMaxConsecutiveDays() != null)
            entity.setMaxConsecutiveDays(dto.getMaxConsecutiveDays());

        if (dto.getApplyBeforeDays() != null)
            entity.setApplyBeforeDays(dto.getApplyBeforeDays());

        if (dto.getAllowBackdatedLeave() != null)
            entity.setAllowBackdatedLeave(dto.getAllowBackdatedLeave());

        if (dto.getSandwichRuleEnabled() != null)
            entity.setSandwichRuleEnabled(dto.getSandwichRuleEnabled());

        if (dto.getIncludeHolidays() != null)
            entity.setIncludeHolidays(dto.getIncludeHolidays());

        if (dto.getIncludeWeekends() != null)
            entity.setIncludeWeekends(dto.getIncludeWeekends());
    }

    // RESPONSE
    public static LeaveApplicationRuleResponseDTO toResponse(LeaveApplicationRule e) {

        return LeaveApplicationRuleResponseDTO.builder()
                .id(e.getId())
                .policyId(e.getLeavePolicy().getId())
                .allowHalfDay(e.getAllowHalfDay())
                .minLeaveDuration(e.getMinLeaveDuration())
                .maxConsecutiveDays(e.getMaxConsecutiveDays())
                .applyBeforeDays(e.getApplyBeforeDays())
                .allowBackdatedLeave(e.getAllowBackdatedLeave())
                .sandwichRuleEnabled(e.getSandwichRuleEnabled())
                .includeHolidays(e.getIncludeHolidays())
                .includeWeekends(e.getIncludeWeekends())
                .isActive(e.getIsActive())
                .build();
    }
}