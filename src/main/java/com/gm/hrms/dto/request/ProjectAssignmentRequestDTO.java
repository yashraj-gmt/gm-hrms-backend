package com.gm.hrms.dto.request;

import lombok.Data;

@Data
public class ProjectAssignmentRequestDTO {

    private Long projectId;
    private Long employeeId;
    private String roleInProject;
}

