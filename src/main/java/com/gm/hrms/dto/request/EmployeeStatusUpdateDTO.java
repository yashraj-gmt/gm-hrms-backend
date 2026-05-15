package com.gm.hrms.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmployeeStatusUpdateDTO {

    @NotBlank(message = "Status is required")
    private String status;
}