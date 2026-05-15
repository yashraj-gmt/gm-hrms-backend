// JasperPayslipReportService.java
package com.gm.hrms.report.service.impl;

import com.gm.hrms.exception.PayslipReportException;
import com.gm.hrms.report.model.PayslipReportData;
import com.gm.hrms.report.service.PayslipReportService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class JasperPayslipReportService implements PayslipReportService {

    private static final String JRXML_PATH = "reports/payslip.jrxml";

    private JasperReport compiledReport;
    private boolean compilationFailed = false;
    private String compilationError;

    @PostConstruct
    public void init() {
        ClassPathResource resource = new ClassPathResource(JRXML_PATH);

        if (!resource.exists()) {
            compilationFailed = true;
            compilationError = "JRXML template not found at classpath: " + JRXML_PATH;
            log.error("Payslip JRXML not found at '{}'. PDF generation will be unavailable.", JRXML_PATH);
            return; // Do NOT throw — let app start, fail gracefully at request time
        }

        try (InputStream stream = resource.getInputStream()) {
            compiledReport = JasperCompileManager.compileReport(stream);
            log.info("Payslip JRXML compiled and cached successfully from '{}'.", JRXML_PATH);

        } catch (JRException ex) {
            compilationFailed = true;
            compilationError = "JRXML compilation failed: " + ex.getMessage();
            // Log with full stack trace for diagnosis, but do NOT crash the application
            log.error("Failed to compile payslip JRXML at '{}'. "
                    + "PDF generation will be unavailable until the template is fixed. "
                    + "Cause: {}", JRXML_PATH, ex.getMessage(), ex);
        } catch (Exception ex) {
            compilationFailed = true;
            compilationError = "Unexpected error loading JRXML: " + ex.getMessage();
            log.error("Unexpected error while loading payslip JRXML: {}", ex.getMessage(), ex);
        }
    }

    @Override
    public byte[] generatePdf(PayslipReportData data) {
        // Guard: compilation must have succeeded
        if (compilationFailed || compiledReport == null) {
            throw new PayslipReportException(
                    "Payslip PDF generation is currently unavailable. "
                            + "Reason: " + compilationError);
        }

        try {
            Map<String, Object> params = buildParams(data);
            JRBeanCollectionDataSource ds =
                    new JRBeanCollectionDataSource(data.getRows());

            JasperPrint print = JasperFillManager.fillReport(compiledReport, params, ds);
            byte[] pdf = JasperExportManager.exportReportToPdf(print);

            log.debug("Payslip PDF generated successfully ({} bytes).", pdf.length);
            return pdf;

        } catch (JRException ex) {
            log.error("JasperReports error while generating payslip PDF: {}", ex.getMessage(), ex);
            throw new PayslipReportException(
                    "Failed to generate payslip PDF: " + ex.getMessage(), ex);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private Map<String, Object> buildParams(PayslipReportData d) {
        Map<String, Object> p = new HashMap<>();

        p.put("COMPANY_NAME",       d.getCompanyName());
        p.put("COMPANY_ADDRESS",    d.getCompanyAddress());
        p.put("LOGO_IMAGE",         d.getLogoImage()); // java.awt.Image
        p.put("PAYSLIP_MONTH_YEAR", d.getPayslipMonthYear());

        p.put("EMPLOYEE_NAME",      d.getEmployeeName());
        p.put("EMPLOYEE_CODE",      d.getEmployeeCode());
        p.put("DEPARTMENT",         d.getDepartment());
        p.put("DESIGNATION",        d.getDesignation());
        p.put("PAY_DATE",           d.getPayDate());
        p.put("DATE_OF_JOINING",    d.getDateOfJoining());
        p.put("GENDER",             d.getGender());

        p.put("BANK_NAME",          d.getBankName());
        p.put("ACCOUNT_NUMBER",     d.getAccountNumber());
        p.put("IFSC_CODE",          d.getIfscCode());
        p.put("PAYMENT_MODE",       d.getPaymentMode());
        p.put("PAN_NUMBER",         d.getPanNumber());
        p.put("PF_NUMBER",          d.getPfNumber());

        p.put("PAID_DAYS",          d.getPaidDays());
        p.put("LOP_DAYS",           d.getLopDays());

        p.put("GROSS_EARNINGS",     d.getGrossEarnings());
        p.put("TOTAL_DEDUCTIONS",   d.getTotalDeductions());
        p.put("NET_PAYABLE",        d.getNetPayable());
        p.put("NET_PAYABLE_WORDS",  d.getNetPayableWords());

        return p;
    }
}