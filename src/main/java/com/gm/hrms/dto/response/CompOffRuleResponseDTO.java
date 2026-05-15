package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompOffRuleResponseDTO {

    private Long id;
    private Long policyId;
    private Boolean isEnabled;
    private Boolean approvalRequired;
    private Integer maxPerMonth;
    private Integer expiryDays;
}