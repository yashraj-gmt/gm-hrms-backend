package com.gm.hrms.dto.response;

import com.gm.hrms.enums.CompOffStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class CompOffResponseDTO {

    private Long id;
    private Long personalId;
    private LocalDate workedDate;
    private Double earnedDays;
    private String reason;
    private CompOffStatus status;
    private Long approvedBy;
    private LocalDateTime approvedAt;
}