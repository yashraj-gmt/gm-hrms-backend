package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class SalaryStructureResponseDTO {
    private Long id;
    private Long personalInformationId;
    private String employeeName;
    private Double monthlyCTC;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private Boolean isActive;
    private List<SalaryStructureDetailResponseDTO> details;
}