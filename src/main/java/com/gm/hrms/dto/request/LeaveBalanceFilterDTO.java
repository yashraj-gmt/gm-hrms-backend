package com.gm.hrms.dto.request;

import lombok.Data;

@Data
public class LeaveBalanceFilterDTO {

    private Long personalId;
    private String employeeName;
    private String employeeCode;
    private String designation;
    private String department;
    private Integer year;
}