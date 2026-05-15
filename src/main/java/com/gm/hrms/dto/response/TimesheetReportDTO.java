package com.gm.hrms.dto.response;

import com.gm.hrms.enums.TimesheetStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TimesheetReportDTO {

    private Long timesheetId;

    private Long personId;

    private String personName;

    private Long projectId;

    private String projectName;

    private LocalDate workDate;

    private String workedTime;

    private TimesheetStatus status;
}