package com.gm.hrms.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CompOffRuleRequestDTO {

    @NotNull
    private Long policyId;

    @NotNull
    private Boolean isEnabled;

    @NotNull
    private Boolean approvalRequired;

    @NotNull
    @Min(0)
    @Max(31)
    private Integer maxPerMonth;

    @NotNull
    @Min(1)
    @Max(365)
    private Integer expiryDays;
}