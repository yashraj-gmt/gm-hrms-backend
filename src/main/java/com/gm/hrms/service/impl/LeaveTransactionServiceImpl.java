package com.gm.hrms.service.impl;

import com.gm.hrms.entity.LeaveBalance;
import com.gm.hrms.entity.LeaveTransaction;
import com.gm.hrms.enums.LeaveTransactionType;
import com.gm.hrms.repository.LeaveTransactionRepository;
import com.gm.hrms.service.LeaveTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LeaveTransactionServiceImpl implements LeaveTransactionService {

    private final LeaveTransactionRepository repository;

    @Override
    @Transactional
    public void log(
            LeaveBalance balance,
            LeaveTransactionType type,
            double days,
            double before,
            double after,
            Long referenceId,
            String remarks
    ) {

        //  VALIDATION
        if (balance == null) {
            throw new IllegalArgumentException("Leave balance required");
        }

        if (type == null) {
            throw new IllegalArgumentException("Transaction type required");
        }

        if (days <= 0) {
            return;
        }

        if (after < 0) {
            throw new IllegalStateException("Invalid balance calculation");
        }

        LeaveTransaction txn = LeaveTransaction.builder()
                .leaveBalance(balance)
                .type(type)
                .days(days)
                .personal(balance.getPersonal())
                .beforeBalance(before)
                .afterBalance(after)
                .referenceId(referenceId)
                .remarks(remarks != null ? remarks : "N/A")
                .transactionDate(LocalDateTime.now())
                .build();

        repository.save(txn);
    }

    //  OPTIONAL HELPER
    public void log(LeaveBalance balance, LeaveTransactionType type, int days) {
        log(balance, type, days, 0, 0, null, null);
    }
}