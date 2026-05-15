package com.gm.hrms.service;

import com.gm.hrms.entity.LeaveBalance;

import java.util.List;

public interface LeaveBalanceService {

    void initializeLeaveBalance(Long personalId, Long policyId);

    List<LeaveBalance> getBalance(Long personalId, Integer year);

    void deductLeave(Long personalId, Long leaveTypeId, double days);

    void restoreLeave(Long personalId, Long leaveTypeId, double days);

    void monthlyAccrual();
}