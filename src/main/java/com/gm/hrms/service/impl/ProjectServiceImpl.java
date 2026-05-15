package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.ProjectRequestDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.dto.response.ProjectResponseDTO;
import com.gm.hrms.entity.Project;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.ProjectMapper;
import com.gm.hrms.repository.ProjectRepository;
import com.gm.hrms.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    @Override
    public ProjectResponseDTO create(ProjectRequestDTO dto) {

        if(projectRepository.existsByProjectCode(dto.getProjectCode())){
            throw new DuplicateResourceException("Project code already exists");
        }

        Project project = ProjectMapper.toEntity(dto);

        return ProjectMapper.toResponse(projectRepository.save(project));
    }

    @Override
    public ProjectResponseDTO update(Long id, ProjectRequestDTO dto) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if(dto.getProjectName()!=null) project.setProjectName(dto.getProjectName());
        if(dto.getDescription()!=null) project.setDescription(dto.getDescription());
        if(dto.getClientName()!=null) project.setClientName(dto.getClientName());
        if(dto.getStartDate()!=null) project.setStartDate(dto.getStartDate());
        if(dto.getEndDate()!=null) project.setEndDate(dto.getEndDate());
        if(dto.getStatus()!=null) project.setStatus(dto.getStatus());

        return ProjectMapper.toResponse(projectRepository.save(project));
    }


    @Override
    public ProjectResponseDTO getById(Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        return ProjectMapper.toResponse(project);
    }

    @Override
    public PageResponseDTO<ProjectResponseDTO> getAll(Pageable pageable) {

        Page<Project> page = projectRepository.findAll(pageable);

        List<ProjectResponseDTO> content = page.getContent()
                .stream()
                .map(ProjectMapper::toResponse)
                .toList();

        return PageResponseDTO.<ProjectResponseDTO>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    @Override
    public void delete(Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        projectRepository.delete(project);
    }
}
