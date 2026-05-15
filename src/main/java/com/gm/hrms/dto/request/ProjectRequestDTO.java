package com.gm.hrms.dto.request;

import com.gm.hrms.enums.ProjectStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProjectRequestDTO {

    private String projectName;
    private String projectCode;
    private String description;
    private String clientName;
    private LocalDate startDate;
    private LocalDate endDate;
    private ProjectStatus status;
}

