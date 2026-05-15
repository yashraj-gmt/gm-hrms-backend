package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.LeaveRequestDTO;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.DayType;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.LeaveValidationEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveValidationEngineImpl implements LeaveValidationEngine {

    private final LeaveApplicationRuleRepository ruleRepo;
    private final LeaveEligibilityRuleRepository eligibilityRuleRepo;
    private final LeaveTypeRepository leaveTypeRepository;
    private final HolidayRepository holidayRepo;
    private final AttendanceRepository attendanceRepo;
    private final WorkProfileRepository workProfileRepository;

    // ================= TOTAL DAYS =================
    @Override
    public double calculateTotalDays(LeaveRequestDTO dto, Long policyId) {

        LeaveApplicationRule rule = ruleRepo
                .findByLeavePolicyIdAndIsActiveTrue(policyId)
                .orElseThrow(() -> new InvalidRequestException("Rule not found"));

        // 🔥 PERFORMANCE FIX (List → Set)
        Set<LocalDate> holidays = holidayRepo.findAll()
                .stream()
                .filter(h -> Boolean.TRUE.equals(h.getIsActive()))
                .map(Holiday::getHolidayDate)
                .collect(Collectors.toSet());

        double total = 0;
        LocalDate date = dto.getStartDate();

        while (!date.isAfter(dto.getEndDate())) {

            boolean isHoliday = holidays.contains(date);
            boolean isWeekend = isWeekend(dto.getPersonalId(), date);

            boolean isSandwich = isSandwichDay(date, dto, isHoliday, isWeekend, rule);

            // 🔥 FINAL FILTER
            if (!isSandwich) {

                if (isHoliday && !Boolean.TRUE.equals(rule.getIncludeHolidays())) {
                    date = date.plusDays(1);
                    continue;
                }

                if (isWeekend && !Boolean.TRUE.equals(rule.getIncludeWeekends())) {
                    date = date.plusDays(1);
                    continue;
                }
            }

            total++;
            date = date.plusDays(1);
        }

        // 🔥 HALF-DAY ADJUST
        if (dto.getStartDayType() != DayType.FULL) total -= 0.5;
        if (dto.getEndDayType() != DayType.FULL) total -= 0.5;

        // 🔥 SAFETY (NEGATIVE PROTECTION)
        return Math.max(total, 0);
    }

    // ================= VALIDATION =================
    @Override
    public void validateBeforeApply(LeaveRequestDTO dto, double totalDays, Long policyId) {

        // 🔥 PROBATION VALIDATION
        validateProbation(dto.getPersonalId(), dto.getLeaveTypeId(), policyId);

        LeaveApplicationRule rule = ruleRepo
                .findByLeavePolicyIdAndIsActiveTrue(policyId)
                .orElseThrow(() -> new InvalidRequestException("Rule not found"));

        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new InvalidRequestException("Invalid date range");
        }

        if (totalDays < rule.getMinLeaveDuration()) {
            throw new InvalidRequestException("Below minimum leave");
        }

        if (totalDays > rule.getMaxConsecutiveDays()) {
            throw new InvalidRequestException("Exceeded max limit");
        }

        if (!Boolean.TRUE.equals(rule.getAllowBackdatedLeave())
                && dto.getStartDate().isBefore(LocalDate.now())) {
            throw new InvalidRequestException("Backdated not allowed");
        }
    }

    // ================= PROBATION VALIDATION =================
    private void validateProbation(Long personalId, Long leaveTypeId, Long policyId) {

        WorkProfile profile = workProfileRepository
                .findByPersonalInformationId(personalId)
                .orElseThrow(() -> new InvalidRequestException("Work profile not found"));

        if (profile.getDateOfJoining() == null) {
            return;
        }

        LeaveEligibilityRule rule = eligibilityRuleRepo
                .findByLeavePolicyIdAndIsActiveTrue(policyId)
                .orElseThrow(() -> new InvalidRequestException("Eligibility rule not found"));

        LocalDate probationEndDate = profile.getDateOfJoining()
                .plusMonths(rule.getProbationPeriodInMonths());

        boolean isOnProbation = LocalDate.now().isBefore(probationEndDate);

        if (!isOnProbation) return;

        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new InvalidRequestException("Leave type not found"));

        if (!Boolean.TRUE.equals(leaveType.getAllowDuringProbation())) {
            throw new InvalidRequestException("This leave is not allowed during probation");
        }
    }

    // ================= ATTENDANCE CONFLICT =================
    @Override
    public void validateAttendanceConflict(Long personalId, LeaveRequestDTO dto) {

        LocalDate date = dto.getStartDate();

        while (!date.isAfter(dto.getEndDate())) {

            LocalDate finalDate = date;

            attendanceRepo
                    .findByPersonalInformationIdAndAttendanceDate(personalId, date)
                    .ifPresent(att -> {
                        if (att.getCheckIn() != null) {
                            throw new InvalidRequestException("Already checked-in on " + finalDate);
                        }
                    });

            date = date.plusDays(1);
        }
    }

    // ================= SANDWICH RULE =================
    private boolean isSandwichDay(LocalDate date,
                                  LeaveRequestDTO dto,
                                  boolean isHoliday,
                                  boolean isWeekend,
                                  LeaveApplicationRule rule) {

        if (!Boolean.TRUE.equals(rule.getSandwichRuleEnabled())) return false;

        if (!(isHoliday || isWeekend)) return false;

        boolean hasPrevLeave =
                !date.minusDays(1).isBefore(dto.getStartDate());

        boolean hasNextLeave =
                !date.plusDays(1).isAfter(dto.getEndDate());

        return hasPrevLeave && hasNextLeave;
    }

    // ================= SHIFT BASED WEEKEND =================
    private boolean isWeekend(Long personalId, LocalDate date) {

        WorkProfile profile = workProfileRepository
                .findByPersonalInformationId(personalId)
                .orElse(null);

        if (profile == null || profile.getShift() == null) {
            return date.getDayOfWeek() == DayOfWeek.SUNDAY;
        }

        Shift shift = profile.getShift();

        return shift.getDayConfigs() != null &&
                shift.getDayConfigs()
                        .stream()
                        .anyMatch(d ->
                                d.getDayOfWeek() == date.getDayOfWeek()
                                        && Boolean.TRUE.equals(d.getIsWeekOff())
                        );
    }
}