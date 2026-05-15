package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TimesheetEntryResponseDTO {

    private Long projectId;

    private String projectName;

    private String workedTime; // HH:mm format


    private String description;

}