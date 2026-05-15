package com.gm.hrms.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SalaryStructureDetailRequestDTO {

    @NotNull
    private Long payrollComponentId;

    @NotNull
    @Min(0)
    private Double amount;
}