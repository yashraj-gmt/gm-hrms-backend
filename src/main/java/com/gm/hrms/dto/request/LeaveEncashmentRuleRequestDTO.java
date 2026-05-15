package com.gm.hrms.dto.request;

import com.gm.hrms.enums.EncashmentTiming;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class LeaveEncashmentRuleRequestDTO {

    @NotNull
    private Long policyId;

    @NotNull
    private Boolean isEnabled;

    @NotNull
    @Min(0)
    @Max(365)
    private Integer maxEncashment;

    @NotNull
    private EncashmentTiming timing;
}