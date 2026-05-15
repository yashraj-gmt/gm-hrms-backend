package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.ProjectRequestDTO;
import com.gm.hrms.dto.response.ProjectResponseDTO;
import com.gm.hrms.entity.Project;

public class ProjectMapper {

    private ProjectMapper() {}

    // ⭐ DTO → ENTITY
    public static Project toEntity(ProjectRequestDTO dto){

        if(dto == null) return null;

        Project project = new Project();

        project.setProjectName(dto.getProjectName());
        project.setProjectCode(dto.getProjectCode());
        project.setDescription(dto.getDescription());
        project.setClientName(dto.getClientName());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setStatus(dto.getStatus());

        return project;
    }

    // ⭐ ENTITY → RESPONSE DTO
    public static ProjectResponseDTO toResponse(Project p){

        if(p == null) return null;

        return ProjectResponseDTO.builder()
                .id(p.getId())
                .projectName(p.getProjectName())
                .projectCode(p.getProjectCode())
                .description(p.getDescription())
                .clientName(p.getClientName())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .status(p.getStatus())
                .build();
    }
}
