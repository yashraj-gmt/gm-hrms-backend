package com.gm.hrms.dto.response;

import com.gm.hrms.enums.EmploymentType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class LeavePolicyResponseDTO {

    private Long id;
    private String policyName;
    private String description;
    private EmploymentType employmentType;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    private Boolean requiresApproval;
    private Boolean allowHalfDay;
    private Boolean allowBackdatedLeave;
    private Boolean sandwichRuleEnabled;

    private Boolean isActive;
    private Boolean isSystemDefined;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}