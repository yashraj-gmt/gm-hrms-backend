package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LeaveTypeResponseDTO {

    private Long id;
    private String name;
    private String code;
    private String description;
    private Boolean isPaid;
    private Boolean allowHalfDay;
    private Boolean isActive;
    private Boolean isCompOff;
    private Boolean allowDuringProbation;
    private Boolean isSystemDefined;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}