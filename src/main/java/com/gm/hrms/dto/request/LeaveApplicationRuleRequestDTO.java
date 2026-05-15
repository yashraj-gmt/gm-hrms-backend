package com.gm.hrms.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class LeaveApplicationRuleRequestDTO {

    @NotNull(message = "Policy id is required")
    private Long policyId;

    private Boolean allowHalfDay;

    @DecimalMin(value = "0.5", message = "Minimum leave duration must be at least 0.5")
    @DecimalMax(value = "365", message = "Invalid leave duration")
    private Double minLeaveDuration;

    @Min(value = 1, message = "Max consecutive days must be at least 1")
    @Max(value = 365, message = "Max consecutive days too large")
    private Integer maxConsecutiveDays;

    @Min(value = 0, message = "Apply before days cannot be negative")
    @Max(value = 365, message = "Apply before days too large")
    private Integer applyBeforeDays;

    private Boolean allowBackdatedLeave;

    private Boolean sandwichRuleEnabled;

    private Boolean includeHolidays;

    private Boolean includeWeekends;
}