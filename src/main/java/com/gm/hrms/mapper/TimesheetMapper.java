package com.gm.hrms.mapper;

import com.gm.hrms.dto.response.TimesheetEntryResponseDTO;
import com.gm.hrms.dto.response.TimesheetResponseDTO;
import com.gm.hrms.entity.Timesheet;
import com.gm.hrms.entity.TimesheetEntry;
import com.gm.hrms.util.TimeUtil;

import java.util.stream.Collectors;

public class TimesheetMapper {

    private TimesheetMapper() {}

    public static TimesheetResponseDTO toResponse(Timesheet t){

        return TimesheetResponseDTO.builder()
                .id(t.getId())
                .workDate(t.getWorkDate())
                .personName(
                        t.getPerson().getFirstName() + " " +
                                t.getPerson().getLastName()
                )
                .status(t.getStatus())
                .totalTime(TimeUtil.toHHmm(t.getTotalMinutes()))
                .entries(
                        t.getEntries()
                                .stream()
                                .map(TimesheetMapper::toEntryDTO) //  use mapper
                                .collect(Collectors.toList())
                )
                .build();
    }
    public static TimesheetEntryResponseDTO toEntryDTO(TimesheetEntry e){

        return TimesheetEntryResponseDTO.builder()
                .projectId(e.getProject().getId())
                .projectName(e.getProject().getProjectName())
                .workedTime(TimeUtil.toHHmm(e.getWorkedMinutes()))
                .description(e.getDescription())
                .build();
    }
}