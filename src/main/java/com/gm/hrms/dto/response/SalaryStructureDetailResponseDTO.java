package com.gm.hrms.dto.response;

import com.gm.hrms.enums.PayrollComponentType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SalaryStructureDetailResponseDTO {
    private Long id;
    private Long payrollComponentId;
    private String componentName;
    private String componentCode;
    private PayrollComponentType componentType;
    private Double amount;
}