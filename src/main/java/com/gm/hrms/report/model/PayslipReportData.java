package com.gm.hrms.report.model;

import lombok.Builder;
import lombok.Getter;

import java.awt.*;
import java.util.List;

@Getter
@Builder
public class PayslipReportData {

    // Company
    private final String companyName;
    private final String companyAddress;
    private final Image logoImage;

    // Period
    private final String payslipMonthYear;

    // Employee
    private final String employeeName;
    private final String employeeCode;
    private final String department;
    private final String designation;
    private final String payDate;
    private final String dateOfJoining;
    private final String gender;

    // Bank
    private final String bankName;
    private final String accountNumber;
    private final String ifscCode;
    private final String paymentMode;
    private final String panNumber;
    private final String pfNumber;

    // Attendance
    private final Integer paidDays;
    private final Integer lopDays;

    // Financials
    private final Double grossEarnings;
    private final Double totalDeductions;
    private final Double netPayable;
    private final String netPayableWords;

    // Component rows
    private final List<PayslipRow> rows;
}