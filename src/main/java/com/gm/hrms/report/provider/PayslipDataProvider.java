package com.gm.hrms.report.provider;

import com.gm.hrms.entity.SalarySlip;
import com.gm.hrms.entity.SalarySlipComponent;
import com.gm.hrms.enums.PayrollComponentType;
import com.gm.hrms.report.model.PayslipReportData;
import com.gm.hrms.report.model.PayslipRow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
@Slf4j
public class PayslipDataProvider {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String COMPANY_NAME    = "G.M. Technosys";
    private static final String COMPANY_ADDRESS =
            "719, 7th Floor, SATYAMEV EMINENCE, Science City Rd, " +
                    "Near Shukan Mall, Sola, Ahmedabad Gujarat 380060.";

    public PayslipReportData buildFrom(SalarySlip slip) {

        List<SalarySlipComponent> earnings   = sorted(slip, PayrollComponentType.EARNING);
        List<SalarySlipComponent> deductions = sorted(slip, PayrollComponentType.DEDUCTION);

        return PayslipReportData.builder()
                // Company
                .companyName(COMPANY_NAME)
                .companyAddress(COMPANY_ADDRESS)
                .logoImage(resolveLogoImage())
                // Period
                .payslipMonthYear(formatMonthYear(slip))
                // Employee
                .employeeName(nullSafe(slip.getEmployeeName()))
                .employeeCode(nullSafe(slip.getEmployeeCode()))
                .department(nullSafe(slip.getDepartment()))
                .designation(nullSafe(slip.getDesignation()))
                .payDate(slip.getPayDate() != null ? slip.getPayDate().format(DATE_FMT) : "")
                .dateOfJoining(slip.getDateOfJoining() != null
                        ? slip.getDateOfJoining().format(DATE_FMT) : "")
                .gender(nullSafe(slip.getGender()))
                // Bank
                .bankName(nullSafe(slip.getBankName()))
                .accountNumber(nullSafe(slip.getAccountNumber()))
                .ifscCode(nullSafe(slip.getIfscCode()))
                .paymentMode("Bank Transfer")
                .panNumber(nullSafe(slip.getPanNumber()))
                .pfNumber(nullSafe(slip.getPfNumber()))
                // Attendance
                .paidDays(slip.getPaidDays() != null ? slip.getPaidDays().intValue() : 0)
                .lopDays(slip.getLopDays()  != null ? slip.getLopDays().intValue()  : 0)
                // Financials
                .grossEarnings(orZero(slip.getGrossEarnings()))
                .totalDeductions(orZero(slip.getTotalDeductions()))
                .netPayable(orZero(slip.getNetPayable()))
                .netPayableWords(nullSafe(slip.getNetPayableWords()))
                // Paired rows
                .rows(buildRows(earnings, deductions))
                .build();
    }

    // ── Private helpers

    private List<SalarySlipComponent> sorted(SalarySlip slip, PayrollComponentType type) {
        return slip.getComponents().stream()
                .filter(c -> c.getType() == type)
                .sorted(Comparator.comparingInt(
                        c -> Optional.ofNullable(c.getDisplayOrder()).orElse(99)))
                .toList();
    }

    private List<PayslipRow> buildRows(List<SalarySlipComponent> earnings,
                                       List<SalarySlipComponent> deductions) {
        int max = Math.max(earnings.size(), deductions.size());
        List<PayslipRow> rows = new ArrayList<>(max);

        for (int i = 0; i < max; i++) {
            rows.add(PayslipRow.builder()
                    .earningName   (i < earnings.size()   ? earnings.get(i).getComponentName()   : "")
                    .earningAmount (i < earnings.size()   ? earnings.get(i).getAmount()           : null)
                    .deductionName (i < deductions.size() ? deductions.get(i).getComponentName() : "")
                    .deductionAmount(i < deductions.size()? deductions.get(i).getAmount()         : null)
                    .build());
        }
        return rows;
    }

    private String formatMonthYear(SalarySlip slip) {
        return Month.of(slip.getMonth())
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                + " " + slip.getYear();
    }

    private Image resolveLogoImage() {
        try {
            byte[] bytes = new ClassPathResource("static/images/gmt-logo.png")
                    .getContentAsByteArray();
            return ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (Exception e) {
            log.warn("Company logo not found at static/images/gmt-logo.png — skipping");
            return null;
        }
    }

    private static String nullSafe(String value) {
        return value != null ? value : "";
    }

    private static double orZero(Double value) {
        return value != null ? value : 0.0;
    }
}