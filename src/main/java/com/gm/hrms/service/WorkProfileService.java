package com.gm.hrms.service;

import com.gm.hrms.dto.request.WorkProfileRequestDTO;
import com.gm.hrms.dto.response.WorkProfileResponseDTO;

import java.util.List;

public interface WorkProfileService {

    WorkProfileResponseDTO create(Long personalInformationId, WorkProfileRequestDTO dto);

    WorkProfileResponseDTO update(Long id, WorkProfileRequestDTO dto);

    WorkProfileResponseDTO getById(Long id);

    List<WorkProfileResponseDTO> getAll();

    void delete(Long id);
}