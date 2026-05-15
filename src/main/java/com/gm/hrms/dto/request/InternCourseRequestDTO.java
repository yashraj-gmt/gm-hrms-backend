package com.gm.hrms.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InternCourseRequestDTO {

    @NotBlank(message = "Course name is required")
    private String name;

    private String description;

    private Boolean status;
}