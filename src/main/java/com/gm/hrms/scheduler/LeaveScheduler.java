package com.gm.hrms.scheduler;

import com.gm.hrms.entity.*;
import com.gm.hrms.enums.LeaveTransactionType;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.LeaveTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LeaveScheduler {

    private final LeaveBalanceRepository balanceRepository;
    private final LeavePolicyLeaveTypeRepository mappingRepository;
    private final CarryForwardRuleRepository carryForwardRepository;
    private final LeaveEncashmentRuleRepository encashmentRepository;
    private final CompOffRequestRepository compOffRepository;
    private final LeaveTransactionService transactionService;

    // ================= 1. MONTHLY ACCRUAL =================
    @Scheduled(cron = "0 0 2 1 * ?")
    public void monthlyAccrual() {

        int year = LocalDate.now().getYear();

        List<LeavePolicyLeaveType> mappings =
                mappingRepository.findByAccrualTypeAndIsActiveTrue(
                        com.gm.hrms.enums.AccrualType.MONTHLY);

        for (LeavePolicyLeaveType m : mappings) {

            List<LeaveBalance> balances =
                    balanceRepository.findByLeaveTypeIdAndYear(
                            m.getLeaveType().getId(), year);

            for (LeaveBalance b : balances) {

                double before = b.getRemainingLeaves();

                if (before >= m.getTotalLeaves()) continue;

                double after = Math.min(
                        before + m.getAccrualValue(),
                        m.getTotalLeaves()
                );

                b.setRemainingLeaves(after);
                balanceRepository.save(b);

                transactionService.log(
                        b,
                        LeaveTransactionType.ACCRUAL,
                        m.getAccrualValue().doubleValue(),
                        before,
                        after,
                        null,
                        "Monthly accrual"
                );
            }
        }
    }

    // ================= 2. YEAR END CARRY FORWARD =================
    @Scheduled(cron = "0 0 3 1 1 ?")
    public void carryForward() {

        int currentYear = LocalDate.now().getYear();
        int previousYear = currentYear - 1;

        List<LeaveBalance> balances =
                balanceRepository.findByYear(previousYear);

        for (LeaveBalance b : balances) {

            CarryForwardRule rule =
                    carryForwardRepository
                            .findByLeavePolicyIdAndIsActiveTrue(
                                    b.getLeavePolicy().getId())
                            .orElse(null);

            if (rule == null || !rule.getIsEnabled()) continue;

            double carry = Math.min(
                    b.getRemainingLeaves(),
                    rule.getMaxCarryForward()
            );

            LeaveBalance newBalance = LeaveBalance.builder()
                    .personal(b.getPersonal())
                    .leavePolicy(b.getLeavePolicy())
                    .leaveType(b.getLeaveType())
                    .totalLeaves(b.getTotalLeaves())
                    .usedLeaves(0.0)
                    .remainingLeaves(carry)
                    .year(currentYear)
                    .build();

            balanceRepository.save(newBalance);
        }
    }

    // ================= 3. LEAVE ENCASHMENT =================
    @Scheduled(cron = "0 0 4 31 12 ?")
    public void encashLeaves() {

        int year = LocalDate.now().getYear();

        List<LeaveBalance> balances =
                balanceRepository.findByYear(year);

        for (LeaveBalance b : balances) {

            LeaveEncashmentRule rule =
                    encashmentRepository
                            .findByLeavePolicyIdAndIsActiveTrue(
                                    b.getLeavePolicy().getId())
                            .orElse(null);

            if (rule == null || !rule.getIsEnabled()) continue;

            double encash = Math.min(
                    b.getRemainingLeaves(),
                    rule.getMaxEncashment()
            );

            double before = b.getRemainingLeaves();

            b.setRemainingLeaves(before - encash);

            balanceRepository.save(b);

            transactionService.log(
                    b,
                    LeaveTransactionType.ENCASHMENT,
                    encash,
                    before,
                    b.getRemainingLeaves(),
                    null,
                    "Year-end encashment"
            );
        }
    }

    // ================= 4. COMPOFF EXPIRY =================
    @Scheduled(cron = "0 0 1 * * ?")
    public void expireCompOff() {

        LocalDate today = LocalDate.now();

        List<CompOffRequest> requests = compOffRepository.findAll();

        for (CompOffRequest r : requests) {

            if (r.getStatus() != com.gm.hrms.enums.CompOffStatus.APPROVED)
                continue;

            if (r.getWorkedDate().plusDays(30).isBefore(today)) {

                LeaveBalance balance =
                        balanceRepository.findByPersonalIdAndYear(
                                r.getPersonal().getId(),
                                today.getYear()
                        ).stream().findFirst().orElse(null);

                if (balance == null) continue;

                double before = balance.getRemainingLeaves();

                double expired = r.getEarnedDays();

                balance.setRemainingLeaves(
                        Math.max(0, before - expired)
                );

                balanceRepository.save(balance);

                transactionService.log(
                        balance,
                        LeaveTransactionType.EXPIRE,
                        expired,
                        before,
                        balance.getRemainingLeaves(),
                        r.getId(),
                        "Comp-off expired"
                );
            }
        }
    }
}