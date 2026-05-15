package com.gm.hrms.dto.response;

import com.gm.hrms.enums.ProjectStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ProjectResponseDTO {

    private Long id;
    private String projectName;
    private String projectCode;
    private String description;
    private String clientName;
    private LocalDate startDate;
    private LocalDate endDate;
    private ProjectStatus status;
}

