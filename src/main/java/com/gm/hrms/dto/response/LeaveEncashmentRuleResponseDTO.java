package com.gm.hrms.dto.response;

import com.gm.hrms.enums.EncashmentTiming;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaveEncashmentRuleResponseDTO {

    private Long id;
    private Long policyId;
    private Boolean isEnabled;
    private Integer maxEncashment;
    private EncashmentTiming timing;
}