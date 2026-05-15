package com.gm.hrms.dto.request;

import com.gm.hrms.enums.AccrualType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LeavePolicyLeaveTypeRequestDTO {

    @NotNull(message = "Policy ID required")
    private Long policyId;

    @NotNull(message = "Leave type ID required")
    private Long leaveTypeId;

    @NotNull(message = "Total leaves required")
    @Min(value = 0, message = "Leaves cannot be negative")
    private Integer totalLeaves;

    @NotNull(message = "Accrual type required")
    private AccrualType accrualType;

    @NotNull(message = "Accrual value required")
    @Min(value = 0, message = "Invalid accrual value")
    private Integer accrualValue;
}