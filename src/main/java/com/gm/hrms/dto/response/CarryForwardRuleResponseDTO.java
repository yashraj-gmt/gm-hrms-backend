package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CarryForwardRuleResponseDTO {

    private Long id;
    private Long policyId;
    private Boolean isEnabled;
    private Integer maxCarryForward;
    private Integer expiryDays;
}