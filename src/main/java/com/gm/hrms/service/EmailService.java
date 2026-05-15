package com.gm.hrms.service;

public interface EmailService {

    void sendCredentials(String to, String name, String password);

    void sendOtp(String to, String name, String otp);

    void sendTransferOtp(String to, String name, String otp, String purpose);

    void sendLeaveAppliedEmployee(String to, String employeeName, String leaveType,
                                  String startDate, String endDate, double days);

    void sendLeaveAppliedManager(String to, String employeeName, String leaveType,
                                 String startDate, String endDate, double days, String reason);

    void sendLeaveStatusUpdate(String to, String employeeName, String leaveType,
                               String status, String startDate, String endDate, String remarks);

    void sendLeaveCancelledManager(String to, String employeeName, String leaveType,
                                   String startDate, String endDate, String cancelReason);

}