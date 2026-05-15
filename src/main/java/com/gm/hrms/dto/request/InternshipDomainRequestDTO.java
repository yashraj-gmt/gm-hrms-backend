package com.gm.hrms.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InternshipDomainRequestDTO {

    @NotBlank
    private String name;

    private String description;

    private Boolean active;
}