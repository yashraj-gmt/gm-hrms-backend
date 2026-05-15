package com.gm.hrms.dto.response;

import com.gm.hrms.enums.PayrollComponentType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PayrollComponentResponseDTO {
    private Long id;
    private String name;
    private String code;
    private PayrollComponentType type;
    private String description;
    private Integer displayOrder;
    private Boolean isSystemDefined;
    private Boolean isActive;
}