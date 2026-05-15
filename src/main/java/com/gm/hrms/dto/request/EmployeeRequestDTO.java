package com.gm.hrms.dto.request;

import com.gm.hrms.enums.RoleType;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class EmployeeRequestDTO {

    private RoleType role;

    @Valid
    private EmployeeEmploymentRequestDTO employment;
}