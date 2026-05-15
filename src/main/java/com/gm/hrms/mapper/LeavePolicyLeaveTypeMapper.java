package com.gm.hrms.mapper;

import com.gm.hrms.dto.response.LeavePolicyLeaveTypeResponseDTO;
import com.gm.hrms.entity.LeavePolicyLeaveType;
import com.gm.hrms.enums.AccrualType;

public class LeavePolicyLeaveTypeMapper {

    private LeavePolicyLeaveTypeMapper() {}

    public static LeavePolicyLeaveTypeResponseDTO toResponse(LeavePolicyLeaveType entity) {

        return LeavePolicyLeaveTypeResponseDTO.builder()
                .id(entity.getId())

                .policyId(entity.getLeavePolicy().getId())
                .policyName(entity.getLeavePolicy().getPolicyName())

                .leaveTypeId(entity.getLeaveType().getId())
                .leaveTypeName(entity.getLeaveType().getName())
                .leaveCode(entity.getLeaveType().getCode())

                .totalLeaves(entity.getTotalLeaves())
                .accrualType(entity.getAccrualType())
                .accrualValue(entity.getAccrualValue())

                .isActive(entity.getIsActive())
                .build();
    }

    public static void updateEntity(LeavePolicyLeaveType entity,
                                    Integer totalLeaves,
                                    AccrualType accrualType,
                                    Integer accrualValue) {

        if (totalLeaves != null) entity.setTotalLeaves(totalLeaves);
        if (accrualType != null) entity.setAccrualType(accrualType);
        if (accrualValue != null) entity.setAccrualValue(accrualValue);
    }
}