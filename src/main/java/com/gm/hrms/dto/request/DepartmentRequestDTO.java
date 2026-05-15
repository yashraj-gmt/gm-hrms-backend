package com.gm.hrms.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DepartmentRequestDTO {

    @NotBlank(message = "Department name is required")
    private String name;

    @NotBlank(message = "Department code is required")
    private String code;

    private String description;

    @NotNull(message = "Status is required")
    private Boolean status;

    @Valid
    private List<SubDepartmentRequestDTO> subDepartments = new ArrayList<>();
}