package com.gm.hrms.dto.response;

import com.gm.hrms.enums.PayrollComponentType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SalarySlipComponentResponseDTO {
    private String componentName;
    private String componentCode;
    private PayrollComponentType type;
    private Double amount;
    private Integer displayOrder;
}