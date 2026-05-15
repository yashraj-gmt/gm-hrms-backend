package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class LeaveRequestResponseDTO {

    private Long id;

    private Long personalId;



    // 🔹 Employee
    private String employeeName;
    private String employeeCode;

    // 🔹 Leave
    private String leaveType;
    private String leaveTypeCode;


    private LocalDate startDate;
    private LocalDate endDate;
    private String designation;
    private String department;

    private String dateRange;

    private double totalDays;

    private String reason;

    // 🔹 Status
    private String status;

    // 🔹 Metadata
    private LocalDateTime appliedOn;

    private String startDayType;
    private String endDayType;
    private String approvedByName;
    private String approvedAt;

    private String rejectionReason;
    private String cancelReason;
    private String cancelledAt;
}