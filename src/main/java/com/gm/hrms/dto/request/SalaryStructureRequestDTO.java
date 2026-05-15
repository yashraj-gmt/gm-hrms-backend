package com.gm.hrms.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SalaryStructureRequestDTO {

    @NotNull
    private LocalDate effectiveFrom;

    @NotNull
    @Min(0)
    private Double monthlyCTC;

    @NotEmpty
    private List<SalaryStructureDetailRequestDTO> details;
}