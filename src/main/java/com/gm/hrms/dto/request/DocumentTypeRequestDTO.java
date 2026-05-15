package com.gm.hrms.dto.request;

import com.gm.hrms.enums.ApplicableType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class DocumentTypeRequestDTO {

    @NotBlank(message = "Document name is required")
    private String name;

    @NotBlank(message = "Document key is required")
    private String key;

    @NotEmpty(message = "At least one applicable type required")
    private Set<ApplicableType> applicableTypes;

    private Boolean mandatory;

    private Boolean active;
}