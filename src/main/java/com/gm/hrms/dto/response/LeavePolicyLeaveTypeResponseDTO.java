package com.gm.hrms.dto.response;

import com.gm.hrms.enums.AccrualType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeavePolicyLeaveTypeResponseDTO {

    private Long id;

    private Long policyId;
    private String policyName;

    private Long leaveTypeId;
    private String leaveTypeName;
    private String leaveCode;

    private Integer totalLeaves;
    private AccrualType accrualType;
    private Integer accrualValue;

    private Boolean isActive;
}