package com.gm.hrms.service;

import com.gm.hrms.entity.LeaveBalance;
import com.gm.hrms.enums.LeaveTransactionType;

public interface LeaveTransactionService {

    public void log(
            LeaveBalance balance,
            LeaveTransactionType type,
            double days,
            double before,
            double after,
            Long referenceId,
            String remarks
    );
}