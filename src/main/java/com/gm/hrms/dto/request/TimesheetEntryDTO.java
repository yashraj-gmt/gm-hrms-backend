package com.gm.hrms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TimesheetEntryDTO {

    @NotNull
    private Long projectId;

    private String workedTime; // HH:mm

    private String taskName;

    private String description;

}