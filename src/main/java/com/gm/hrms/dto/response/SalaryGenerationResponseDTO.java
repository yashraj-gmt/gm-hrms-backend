package com.gm.hrms.dto.response;

import com.gm.hrms.enums.SalaryGenerationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SalaryGenerationResponseDTO {
    private Long id;
    private Integer month;
    private Integer year;
    private String monthYear;
    private SalaryGenerationStatus status;
    private Integer totalEmployees;
    private Double totalGrossPayout;
    private Double totalNetPayout;
    private LocalDateTime generatedAt;
    private LocalDateTime finalizedAt;
    private List<SalarySlipResponseDTO> salarySlips;
}