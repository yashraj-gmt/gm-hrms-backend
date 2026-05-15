package com.gm.hrms.dto.request;

import com.gm.hrms.enums.TimesheetStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TimesheetRequestDTO {

    @NotNull
    private Long personId;

    @NotNull
    private LocalDate workDate;

    @NotNull
    private TimesheetStatus status;

    private List<TimesheetEntryDTO> entries;

}