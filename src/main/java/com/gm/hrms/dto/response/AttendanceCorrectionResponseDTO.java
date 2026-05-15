// src/main/java/com/gm/hrms/dto/response/AttendanceCorrectionResponseDTO.java
package com.gm.hrms.dto.response;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceCorrectionResponseDTO {
    private Long id;
    private LocalDate attendanceDate;
    private String employeeName;
    private String employeeCode;
    private String department;
    private String designation;
    private LocalDateTime originalCheckIn;
    private LocalDateTime originalCheckOut;
    private LocalDateTime requestedCheckIn;
    private LocalDateTime requestedCheckOut;
    private String reason;
    private String status;
    private String remarks;
    private String reviewedBy;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
}