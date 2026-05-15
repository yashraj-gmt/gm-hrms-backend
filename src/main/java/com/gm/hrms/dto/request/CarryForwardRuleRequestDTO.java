package com.gm.hrms.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CarryForwardRuleRequestDTO {

    @NotNull
    private Long policyId;

    @NotNull
    private Boolean isEnabled;

    @NotNull
    @Min(0)
    @Max(365)
    private Integer maxCarryForward;

    @Min(0)
    @Max(365)
    private Integer expiryDays;
}