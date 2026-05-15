package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaveApplicationRuleResponseDTO {

    private Long id;
    private Long policyId;

    private Boolean allowHalfDay;
    private Double minLeaveDuration;
    private Integer maxConsecutiveDays;
    private Integer applyBeforeDays;
    private Boolean allowBackdatedLeave;

    private Boolean sandwichRuleEnabled;
    private Boolean includeHolidays;
    private Boolean includeWeekends;

    private Boolean isActive;
}