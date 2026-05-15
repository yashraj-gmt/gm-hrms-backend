package com.gm.hrms.dto.response;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftAssignmentResponseDTO {
    private Long id;
    private Long shiftId;
    private String shiftName;
    private String shiftType;
    private String shiftTiming;       // "09:00 – 18:00" or "Day-wise Config"
    private Long personalInformationId;
    private String employeeName;
    private String employeeCode;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private String status;
    private String note;
    private ShiftResponseDTO shiftDetails;
}