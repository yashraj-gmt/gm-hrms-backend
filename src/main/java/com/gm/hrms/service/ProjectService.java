package com.gm.hrms.service;

import com.gm.hrms.dto.request.ProjectRequestDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.dto.response.ProjectResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProjectService {

    ProjectResponseDTO create(ProjectRequestDTO dto);

    ProjectResponseDTO update(Long id, ProjectRequestDTO dto);

    ProjectResponseDTO getById(Long id);

    PageResponseDTO<ProjectResponseDTO> getAll(Pageable pageable);

    void delete(Long id);
}

