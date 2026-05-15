package com.gm.hrms.mapper;

import com.gm.hrms.dto.response.AttendanceResponseDTO;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.AttendanceLogType;
import com.gm.hrms.enums.AttendanceStatus;

import java.time.LocalDateTime;

public final class AttendanceMapper {

    private AttendanceMapper(){}

    public static Attendance createAttendance(
            PersonalInformation person,
            WorkProfile profile
    ){

        LocalDateTime now = LocalDateTime.now();

        return Attendance.builder()
                .personalInformation(person)
                .workProfile(profile)
                .attendanceDate(now.toLocalDate())
                .checkIn(now)
                .status(AttendanceStatus.PRESENT)
                .build();
    }

    public static AttendanceLog createCheckInLog(PersonalInformation person){

        return AttendanceLog.builder()
                .personalInformation(person)
                .logTime(LocalDateTime.now())
                .logType(AttendanceLogType.CHECK_IN)
                .deviceType("WEB")
                .build();
    }

    public static AttendanceLog createCheckOutLog(PersonalInformation person){

        return AttendanceLog.builder()
                .personalInformation(person)
                .logTime(LocalDateTime.now())
                .logType(AttendanceLogType.CHECK_OUT)
                .deviceType("WEB")
                .build();
    }

    public static AttendanceBreakLog createBreakStart(Attendance attendance){

        return AttendanceBreakLog.builder()
                .attendance(attendance)
                .breakStart(LocalDateTime.now())
                .build();
    }

    public static AttendanceCalculation createCalculation(
            Attendance attendance,
            int workMinutes,
            int breakMinutes,
            int lateMinutes,
            int overtimeMinutes
    ){

        return AttendanceCalculation.builder()
                .attendance(attendance)
                .workMinutes(workMinutes)
                .breakMinutes(breakMinutes)
                .lateMinutes(lateMinutes)
                .overtimeMinutes(overtimeMinutes)
                .build();
    }

    public static AttendanceResponseDTO toResponse(
            Attendance attendance,
            AttendanceCalculation calc
    ){

        Long personalId = null;

        if(attendance.getPersonalInformation() != null){
            personalId = attendance.getPersonalInformation().getId();
        }

        return AttendanceResponseDTO.builder()
                .id(attendance.getId())
                .personalInformationId(personalId)
                .checkIn(attendance.getCheckIn())
                .checkOut(attendance.getCheckOut())
                .workMinutes(calc != null ? calc.getWorkMinutes() : 0)
                .breakMinutes(calc != null ? calc.getBreakMinutes() : 0)
                .lateMinutes(calc != null ? calc.getLateMinutes() : 0)
                .overtimeMinutes(calc != null ? calc.getOvertimeMinutes() : 0)
                .status(attendance.getStatus())
                .build();
    }
}