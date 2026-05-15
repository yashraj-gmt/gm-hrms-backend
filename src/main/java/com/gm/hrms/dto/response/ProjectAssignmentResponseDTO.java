package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectAssignmentResponseDTO {

    private Long id;
    private Long projectId;
    private String projectName;

    private Long employeeId;
    private String employeeName;

    private String roleInProject;
}

