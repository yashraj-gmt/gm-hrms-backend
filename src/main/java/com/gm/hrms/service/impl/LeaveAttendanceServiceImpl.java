package com.gm.hrms.service.impl;

import com.gm.hrms.entity.*;
import com.gm.hrms.enums.AttendanceStatus;
import com.gm.hrms.enums.DayType;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.LeaveAttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveAttendanceServiceImpl implements LeaveAttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final HolidayRepository holidayRepository;
    private final WorkProfileRepository workProfileRepository;
    private final LeaveApplicationRuleRepository ruleRepository;

    // ================= MARK ATTENDANCE =================
    @Override
    public void markLeaveAttendance(LeaveRequest req) {

        Long personalId = req.getPersonalId();

        LeaveApplicationRule rule = ruleRepository
                .findByLeavePolicyIdAndIsActiveTrue(req.getLeavePolicy().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave application rule not found"));

        List<LocalDate> holidays = holidayRepository.findAll()
                .stream()
                .filter(h -> Boolean.TRUE.equals(h.getIsActive()))
                .map(Holiday::getHolidayDate)
                .toList();

        WorkProfile profile = workProfileRepository
                .findByPersonalInformationId(personalId)
                .orElse(null);

        LocalDate date = req.getStartDate();

        while (!date.isAfter(req.getEndDate())) {

            boolean isHoliday = holidays.contains(date);
            boolean isWeekend = isWeekend(date, profile);

            boolean isSandwich = isSandwichDay(date, req, isHoliday, isWeekend, rule);

            // ================= FILTER =================
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

            LocalDate currentDate = date;

            Attendance attendance = attendanceRepository
                    .findByPersonalInformationIdAndAttendanceDate(personalId, currentDate)
                    .orElseGet(() -> Attendance.builder()
                            .personalInformation(
                                    PersonalInformation.builder()
                                            .id(personalId)
                                            .build()
                            )
                            .attendanceDate(currentDate)
                            .build()
                    );

            AttendanceStatus newStatus = resolveStatus(req, currentDate);

            // 🔥 overwrite protection
            if (attendance.getStatus() == null ||
                    attendance.getStatus() == AttendanceStatus.PRESENT) {

                attendance.setStatus(newStatus);
            }

            attendanceRepository.save(attendance);

            date = date.plusDays(1);
        }
    }

    // ================= REVERT =================
    @Override
    public void revertLeaveAttendance(LeaveRequest req) {

        LocalDate date = req.getStartDate();

        while (!date.isAfter(req.getEndDate())) {

            attendanceRepository
                    .findByPersonalInformationIdAndAttendanceDate(
                            req.getPersonalId(),
                            date
                    )
                    .ifPresent(a -> {
                        a.setStatus(AttendanceStatus.PRESENT);
                        attendanceRepository.save(a);
                    });

            date = date.plusDays(1);
        }
    }

    // ================= STATUS =================
    private AttendanceStatus resolveStatus(LeaveRequest req, LocalDate date) {

        // single day
        if (req.getStartDate().equals(req.getEndDate())) {

            if (req.getStartDayType() != DayType.FULL) {
                return AttendanceStatus.HALF_DAY;
            }
            return AttendanceStatus.LEAVE;
        }

        // first day
        if (date.equals(req.getStartDate())) {
            return req.getStartDayType() == DayType.FULL
                    ? AttendanceStatus.LEAVE
                    : AttendanceStatus.HALF_DAY;
        }

        // last day
        if (date.equals(req.getEndDate())) {
            return req.getEndDayType() == DayType.FULL
                    ? AttendanceStatus.LEAVE
                    : AttendanceStatus.HALF_DAY;
        }

        // middle days
        return AttendanceStatus.LEAVE;
    }

    // ================= SANDWICH RULE =================
    private boolean isSandwichDay(LocalDate date,
                                  LeaveRequest req,
                                  boolean isHoliday,
                                  boolean isWeekend,
                                  LeaveApplicationRule rule) {

        if (!Boolean.TRUE.equals(rule.getSandwichRuleEnabled())) return false;

        if (!(isHoliday || isWeekend)) return false;

        boolean hasPrevLeave =
                !date.minusDays(1).isBefore(req.getStartDate());

        boolean hasNextLeave =
                !date.plusDays(1).isAfter(req.getEndDate());

        return hasPrevLeave && hasNextLeave;
    }

    // ================= SHIFT WEEKEND =================
    private boolean isWeekend(LocalDate date, WorkProfile profile) {

        // fallback
        if (profile == null || profile.getShift() == null) {
            return date.getDayOfWeek() == DayOfWeek.SUNDAY;
        }

        Shift shift = profile.getShift();

        // use ShiftDayConfig
        return shift.getDayConfigs() != null &&
                shift.getDayConfigs()
                        .stream()
                        .anyMatch(d ->
                                d.getDayOfWeek() == date.getDayOfWeek()
                                        && Boolean.TRUE.equals(d.getIsWeekOff())
                        );
    }
}