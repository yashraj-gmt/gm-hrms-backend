package com.gm.hrms.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EligiblePersonDTO {
    private Long personalInformationId;
    private String code;          // EMP001 / INT001 / TRN001
    private String fullName;
    private String department;
    private String designation;
    private String currentShift;  // name of their active shift, or "Unassigned"
    private String employmentType; // EMPLOYEE / INTERN / TRAINEE
}