package com.gm.hrms.dto.request;

import com.gm.hrms.enums.EmploymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LeavePolicyRequestDTO {

    @NotBlank(message = "Policy name is required")
    private String policyName;

    private String description;

    @NotNull(message = "Employment type is required")
    private EmploymentType employmentType;

    @NotNull(message = "Effective from date required")
    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    // RULES
    private Boolean requiresApproval;
    private Boolean allowHalfDay;
    private Boolean allowBackdatedLeave;
    private Boolean sandwichRuleEnabled;
}