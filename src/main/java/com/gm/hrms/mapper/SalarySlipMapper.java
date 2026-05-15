package com.gm.hrms.mapper;

import com.gm.hrms.dto.response.SalarySlipComponentResponseDTO;
import com.gm.hrms.dto.response.SalarySlipResponseDTO;
import com.gm.hrms.entity.SalarySlip;
import com.gm.hrms.entity.SalarySlipComponent;
import com.gm.hrms.enums.PayrollComponentType;

import java.time.Month;
import java.util.List;

public class SalarySlipMapper {

    public static SalarySlipResponseDTO toResponse(SalarySlip slip) {

        List<SalarySlipComponentResponseDTO> earnings = slip.getComponents().stream()
                .filter(c -> c.getType() == PayrollComponentType.EARNING)
                .map(SalarySlipMapper::toComponentResponse)
                .toList();

        List<SalarySlipComponentResponseDTO> deductions = slip.getComponents().stream()
                .filter(c -> c.getType() == PayrollComponentType.DEDUCTION)
                .map(SalarySlipMapper::toComponentResponse)
                .toList();

        String monthYear = Month.of(slip.getMonth()).name().charAt(0)
                + Month.of(slip.getMonth()).name().substring(1).toLowerCase()
                + " " + slip.getYear();

        return SalarySlipResponseDTO.builder()
                .id(slip.getId())
                .salaryGenerationId(slip.getSalaryGeneration().getId())
                .employeeCode(slip.getEmployeeCode())
                .employeeName(slip.getEmployeeName())
                .department(slip.getDepartment())
                .designation(slip.getDesignation())
                .gender(slip.getGender())
                .dateOfJoining(slip.getDateOfJoining())
                .payDate(slip.getPayDate())
                .bankName(slip.getBankName())
                .accountNumber(slip.getAccountNumber())
                .ifscCode(slip.getIfscCode())
                .panNumber(slip.getPanNumber())
                .pfNumber(slip.getPfNumber())
                .month(slip.getMonth())
                .year(slip.getYear())
                .monthYear(monthYear)
                .totalWorkingDays(slip.getTotalWorkingDays())
                .paidDays(slip.getPaidDays())
                .lopDays(slip.getLopDays())
                .grossEarnings(slip.getGrossEarnings())
                .totalDeductions(slip.getTotalDeductions())
                .netPayable(slip.getNetPayable())
                .netPayableWords(slip.getNetPayableWords())
                .status(slip.getStatus())
                .earnings(earnings)
                .deductions(deductions)
                .build();
    }

    private static SalarySlipComponentResponseDTO toComponentResponse(SalarySlipComponent c) {
        return SalarySlipComponentResponseDTO.builder()
                .componentName(c.getComponentName())
                .componentCode(c.getComponentCode())
                .type(c.getType())
                .amount(c.getAmount())
                .displayOrder(c.getDisplayOrder())
                .build();
    }
}