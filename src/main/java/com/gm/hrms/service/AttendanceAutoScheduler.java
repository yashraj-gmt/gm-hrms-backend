package com.gm.hrms.service;

import com.gm.hrms.entity.Attendance;
import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.enums.AttendanceStatus;
import com.gm.hrms.repository.AttendanceRepository;
import com.gm.hrms.repository.HolidayRepository;
import com.gm.hrms.repository.PersonalInformationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceAutoScheduler {

    private final PersonalInformationRepository personalInformationRepository;
    private final AttendanceRepository attendanceRepository;
    private final HolidayRepository holidayRepository;

    @Scheduled(cron = "0 59 23 * * ?")
    public void generateAttendance(){

        LocalDate today = LocalDate.now();

        List<PersonalInformation> users =
                personalInformationRepository.findAll();

        for(PersonalInformation person : users){

            boolean exists =
                    attendanceRepository
                            .existsByPersonalInformationIdAndAttendanceDate(
                                    person.getId(),
                                    today);

            if(exists) continue;

            AttendanceStatus status = AttendanceStatus.ABSENT;

            if(holidayRepository.existsByHolidayDate(today)){
                status = AttendanceStatus.HOLIDAY;
            }

            Attendance attendance =
                    Attendance.builder()
                            .personalInformation(person)
                            .attendanceDate(today)
                            .status(status)
                            .build();

            attendanceRepository.save(attendance);
        }
    }
}