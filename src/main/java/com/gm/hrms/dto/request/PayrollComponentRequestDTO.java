package com.gm.hrms.dto.request;

import com.gm.hrms.enums.PayrollComponentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PayrollComponentRequestDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String code;

    @NotNull
    private PayrollComponentType type;

    private String description;

    private Integer displayOrder;
}