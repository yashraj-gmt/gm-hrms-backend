package com.gm.hrms.mapper;


import com.gm.hrms.dto.response.ProjectAssignmentResponseDTO;
import com.gm.hrms.entity.ProjectAssignment;

public class ProjectAssignmentMapper {

    private ProjectAssignmentMapper(){}

    public static ProjectAssignmentResponseDTO toResponse(ProjectAssignment pa){

        if(pa == null) return null;

        return ProjectAssignmentResponseDTO.builder()
                .id(pa.getId())
                .projectId(pa.getProject().getId())
                .projectName(pa.getProject().getProjectName())
                .employeeId(pa.getEmployee().getId())
//                .employeeName(pa.getEmployee().getFirstName() + " " + pa.getEmployee().getLastName())
                .roleInProject(pa.getRoleInProject())
                .build();
    }
}

