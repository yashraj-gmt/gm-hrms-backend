package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.LeaveEligibilityRuleRequestDTO;
import com.gm.hrms.dto.response.LeaveEligibilityRuleResponseDTO;
import com.gm.hrms.entity.LeaveEligibilityRule;
import com.gm.hrms.entity.LeavePolicy;
public class LeaveEligibilityRuleMapper {

    private LeaveEligibilityRuleMapper() {}

    // ================= CREATE =================
    public static LeaveEligibilityRule toEntity(
            LeaveEligibilityRuleRequestDTO dto,
            LeavePolicy policy
    ) {
        return LeaveEligibilityRule.builder()
                .leavePolicy(policy)
                .probationPeriodInMonths(dto.getProbationPeriodInMonths())
                .allowCompOff(dto.getAllowCompOff())
                .isActive(true)
                .isSystemDefined(false)
                .build();
    }

    // ================= PATCH UPDATE =================
    public static void patchUpdate(LeaveEligibilityRule entity, LeaveEligibilityRuleRequestDTO dto) {

        if (dto.getProbationPeriodInMonths() != null) {
            entity.setProbationPeriodInMonths(dto.getProbationPeriodInMonths());
        }

        if (dto.getAllowCompOff() != null) {
            entity.setAllowCompOff(dto.getAllowCompOff());
        }
    }

    // ================= RESPONSE =================
    public static LeaveEligibilityRuleResponseDTO toResponse(LeaveEligibilityRule e) {

        return LeaveEligibilityRuleResponseDTO.builder()
                .id(e.getId())
                .policyId(e.getLeavePolicy().getId())
                .probationPeriodInMonths(e.getProbationPeriodInMonths())
                .allowCompOff(e.getAllowCompOff())
                .isActive(e.getIsActive())
                .build();
    }
}