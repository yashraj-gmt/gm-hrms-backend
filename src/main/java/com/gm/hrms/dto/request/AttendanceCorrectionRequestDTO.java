package com.gm.hrms.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AttendanceCorrectionRequestDTO {

    private Long attendanceId;

    private LocalDateTime checkIn;

    private LocalDateTime checkOut;

    private String reason;
}