package com.gm.hrms.mapper;

import com.gm.hrms.dto.response.TimesheetReportDTO;
import com.gm.hrms.entity.Timesheet;
import com.gm.hrms.entity.TimesheetEntry;
import com.gm.hrms.util.TimeUtil;
import org.springframework.stereotype.Component;

@Component
public class TimesheetReportMapper {

    public TimesheetReportDTO toDTO(Timesheet t) {

        return TimesheetReportDTO.builder()
                .timesheetId(t.getId())
                .personId(t.getPerson().getId())
                .personName(
                        t.getPerson().getFirstName() + " " +
                                t.getPerson().getLastName()
                )
                .workDate(t.getWorkDate())
                .workedTime(
                        TimeUtil.toHHmm(
                                t.getTotalMinutes() == null ? 0 : t.getTotalMinutes()
                        )
                )
                .status(t.getStatus())
                .build();
    }

    //  Project report mapper
    public TimesheetReportDTO toProjectDTO(TimesheetEntry e) {

        return TimesheetReportDTO.builder()
                .timesheetId(e.getTimesheet().getId())
                .personId(e.getTimesheet().getPerson().getId())
                .personName(
                        e.getTimesheet().getPerson().getFirstName() + " " +
                                e.getTimesheet().getPerson().getLastName()
                )
                .projectId(e.getProject().getId())
                .projectName(e.getProject().getProjectName())
                .workDate(e.getTimesheet().getWorkDate())
                .workedTime(TimeUtil.toHHmm(e.getWorkedMinutes()))
                .status(e.getTimesheet().getStatus())
                .build();
    }
}