package com.gm.hrms.exception;

public class PayslipReportException extends RuntimeException {

    public PayslipReportException(String message) {
        super(message);
    }

    public PayslipReportException(String message, Throwable cause) {
        super(message, cause);
    }
}