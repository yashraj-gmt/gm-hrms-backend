package com.gm.hrms.dto.response;

import com.gm.hrms.enums.TimesheetStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class TimesheetResponseDTO {

    private Long id;

    private LocalDate workDate;

    private String personName;

    private String totalTime;

    private TimesheetStatus status;

    private List<TimesheetEntryResponseDTO> entries;

}
