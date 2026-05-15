package com.gm.hrms.dto.request;

import com.gm.hrms.enums.LeaveTransactionType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveTransactionFilterDTO {

    // 🔹 Employee Filters
    private Long personalId;
    private String employeeName;
    private String employeeCode;
    private String designation;
    private String department;

    // 🔹 Leave Filters
    private Long leaveTypeId;
    private LeaveTransactionType transactionType;

    // 🔹 Date Range
    private LocalDate fromDate;
    private LocalDate toDate;
}