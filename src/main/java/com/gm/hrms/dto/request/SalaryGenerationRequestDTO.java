package com.gm.hrms.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SalaryGenerationRequestDTO {

    @NotNull
    @Min(1) @Max(12)
    private Integer month;

    @NotNull
    @Min(2000)
    private Integer year;
}