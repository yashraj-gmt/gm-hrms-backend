package com.gm.hrms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubDepartmentRequestDTO {

    private Long id;

    @NotBlank(message = "Sub-department name is required")
    private String name;

    @NotBlank(message = "Sub-department code is required")
    private String code;

    private String description;

    @NotNull(message = "Status is required")
    private Boolean status;
}