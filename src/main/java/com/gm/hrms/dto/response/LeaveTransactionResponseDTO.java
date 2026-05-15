package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LeaveTransactionResponseDTO {

    // 🔹 Employee Info
    private String employeeName;
    private String employeeCode;
    private String designation;
    private String department;

    // 🔹 Leave Info
    private String leaveType;
    private String transactionType;

    // 🔹 Transaction Data
    private double days;
    private double beforeBalance;
    private double afterBalance;

    // 🔹 Metadata
    private LocalDateTime date;
    private String remarks;
}