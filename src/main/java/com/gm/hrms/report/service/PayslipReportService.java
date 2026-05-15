package com.gm.hrms.report.service;

import com.gm.hrms.report.model.PayslipReportData;

public interface PayslipReportService {

    byte[] generatePdf(PayslipReportData data);
}