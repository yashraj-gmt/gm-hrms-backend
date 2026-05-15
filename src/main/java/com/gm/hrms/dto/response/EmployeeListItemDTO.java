package com.gm.hrms.dto.response;

import com.gm.hrms.enums.EmploymentType;
import com.gm.hrms.enums.RecordStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class EmployeeListItemDTO {

    private Long   id;
    private String employeeCode;

    // Personal
    private String fullName;
    private String profileImageUrl;

    // Work profile
    private String departmentName;
    private String designationName;
    private String branchName;
    private String shiftTiming;

    // Contact
    private String email;
    private String phone;

    // Employment
    private LocalDate       joiningDate;
    private String          status;          // ACTIVE | INACTIVE | ON_HOLD
    private EmploymentType  employmentType;  // EMPLOYEE | INTERN | TRAINEE
    private RecordStatus    recordStatus;    // DRAFT | SUBMITTED
    private Boolean         active;
}