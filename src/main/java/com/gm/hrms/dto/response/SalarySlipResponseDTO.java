package com.gm.hrms.dto.response;

import com.gm.hrms.enums.SalarySlipStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class SalarySlipResponseDTO {
    private Long id;
    private Long salaryGenerationId;
    private String employeeCode;
    private String employeeName;
    private String department;
    private String designation;
    private String gender;
    private LocalDate dateOfJoining;
    private LocalDate payDate;
    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private String panNumber;
    private String pfNumber;
    private Integer month;
    private Integer year;
    private String monthYear;                   // e.g. "January 2026"
    private Integer totalWorkingDays;
    private Double paidDays;
    private Double lopDays;
    private Double grossEarnings;
    private Double totalDeductions;
    private Double netPayable;
    private String netPayableWords;
    private SalarySlipStatus status;
    private List<SalarySlipComponentResponseDTO> earnings;
    private List<SalarySlipComponentResponseDTO> deductions;
}
