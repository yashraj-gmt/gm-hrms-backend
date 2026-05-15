package com.gm.hrms.service.impl;

import com.gm.hrms.entity.*;
import com.gm.hrms.enums.AccrualType;
import com.gm.hrms.enums.LeaveTransactionType;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.LeaveBalanceService;
import com.gm.hrms.service.LeaveTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

    private final LeaveBalanceRepository repository;
    private final LeavePolicyLeaveTypeRepository mappingRepository;
    private final LeavePolicyRepository policyRepository;
    private final PersonalInformationRepository personalRepository;
    private final LeaveTransactionService leaveTransactionService;

    // ================= INITIALIZE =================
    @Override
    @Transactional
    public void initializeLeaveBalance(Long personalId, Long policyId) {

        PersonalInformation personal = personalRepository.findById(personalId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LeavePolicy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));

        List<LeavePolicyLeaveType> mappings =
                mappingRepository.findByLeavePolicyIdAndIsActiveTrue(policyId);

        int year = LocalDate.now().getYear();

        List<LeaveBalance> toSave = new ArrayList<>();

        for (LeavePolicyLeaveType m : mappings) {

            boolean exists = repository
                    .findByPersonalIdAndLeaveTypeIdAndYear(
                            personalId,
                            m.getLeaveType().getId(),
                            year
                    ).isPresent();

            if (exists) continue;

            double initial = m.getAccrualType() == AccrualType.YEARLY
                    ? m.getTotalLeaves().doubleValue()
                    : 0.0;

            LeaveBalance balance = LeaveBalance.builder()
                    .personal(personal)
                    .leavePolicy(policy)
                    .leaveType(m.getLeaveType())
                    .totalLeaves(m.getTotalLeaves().doubleValue())
                    .usedLeaves(0.0)
                    .remainingLeaves(initial)
                    .year(year)
                    .build();

            toSave.add(balance);
        }

        List<LeaveBalance> savedBalances = repository.saveAll(toSave);

        for (LeaveBalance b : savedBalances) {
            leaveTransactionService.log(
                    b,
                    LeaveTransactionType.ACCRUAL,
                    b.getRemainingLeaves(),
                    0,
                    b.getRemainingLeaves(),
                    null,
                    "Initial balance created"
            );
        }
    }

    // ================= GET =================
    @Override
    public List<LeaveBalance> getBalance(Long personalId, Integer year) {
        return repository.findByPersonalIdAndYear(personalId, year);
    }

    // ================= DEDUCT =================
    @Override
    @Transactional
    public void deductLeave(Long personalId, Long leaveTypeId, double days) {

        if (days <= 0) {
            throw new InvalidRequestException("Days must be greater than 0");
        }

        LeaveBalance balance = getBalanceEntity(personalId, leaveTypeId);

        boolean isCompOffLeave = Boolean.TRUE.equals(balance.getLeaveType().getIsCompOff());

        double before = balance.getRemainingLeaves();

        if (before < days) {
            throw new InvalidRequestException("Insufficient leave balance");
        }

        balance.setUsedLeaves(balance.getUsedLeaves() + days);
        balance.setRemainingLeaves(before - days);

        repository.save(balance);

        leaveTransactionService.log(
                balance,
                LeaveTransactionType.APPLY,
                days,
                before,
                balance.getRemainingLeaves(),
                null,
                isCompOffLeave ? "CompOff used" : "Leave applied"
        );
    }

    // ================= RESTORE =================
    @Override
    @Transactional
    public void restoreLeave(Long personalId, Long leaveTypeId, double days) {

        if (days <= 0) {
            throw new InvalidRequestException("Days must be greater than 0");
        }

        LeaveBalance balance = getBalanceEntity(personalId, leaveTypeId);

        double before = balance.getRemainingLeaves();

        double after = Math.min(
                before + days,
                balance.getTotalLeaves()
        );

        balance.setUsedLeaves(Math.max(0, balance.getUsedLeaves() - days));
        balance.setRemainingLeaves(after);

        repository.save(balance);

        leaveTransactionService.log(
                balance,
                LeaveTransactionType.CANCEL,
                days,
                before,
                after,
                null,
                "Leave cancelled"
        );
    }

    // ================= MONTHLY ACCRUAL =================
    @Override
    @Transactional
    public void monthlyAccrual() {

        int year = LocalDate.now().getYear();

        List<LeavePolicyLeaveType> mappings =
                mappingRepository.findByAccrualTypeAndIsActiveTrue(AccrualType.MONTHLY);

        for (LeavePolicyLeaveType m : mappings) {

            if (m.getAccrualValue() == null || m.getAccrualValue() <= 0) continue;

            List<LeaveBalance> balances =
                    repository.findByLeaveTypeIdAndYear(
                            m.getLeaveType().getId(),
                            year
                    );

            List<LeaveBalance> toUpdate = new ArrayList<>();

            for (LeaveBalance b : balances) {

                double before = b.getRemainingLeaves();

                if (before >= m.getTotalLeaves()) continue;

                double after = Math.min(
                        before + m.getAccrualValue().doubleValue(),
                        m.getTotalLeaves().doubleValue()
                );

                b.setRemainingLeaves(after);

                toUpdate.add(b);

                leaveTransactionService.log(
                        b,
                        LeaveTransactionType.ACCRUAL,
                        m.getAccrualValue().doubleValue(),
                        before,
                        after,
                        null,
                        "Monthly accrual"
                );
            }

            repository.saveAll(toUpdate);
        }
    }

    // ================= COMMON =================
    private LeaveBalance getBalanceEntity(Long personalId, Long leaveTypeId) {

        return repository
                .findByPersonalIdAndLeaveTypeIdAndYear(
                        personalId,
                        leaveTypeId,
                        LocalDate.now().getYear()
                )
                .orElseThrow(() -> new ResourceNotFoundException("Balance not found"));
    }
}