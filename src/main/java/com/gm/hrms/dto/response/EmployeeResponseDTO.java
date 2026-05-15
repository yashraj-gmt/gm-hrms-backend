package com.gm.hrms.dto.response;

import com.gm.hrms.enums.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class EmployeeResponseDTO extends BaseUserResponseDTO {

    private Long employeeId;

    private String employeeCode;
    private String branchName;
    private String shiftTiming;
    private WorkMode workMode;
    private WorkingType workingType;

    private EmployeeEmploymentResponseDTO employment;
}