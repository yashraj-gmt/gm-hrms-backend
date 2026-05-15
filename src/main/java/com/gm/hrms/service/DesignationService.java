package com.gm.hrms.service;

import com.gm.hrms.dto.request.DesignationRequestDTO;
import com.gm.hrms.dto.response.DesignationResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DesignationService {

    DesignationResponseDTO create(DesignationRequestDTO dto);

    DesignationResponseDTO update(Long id, DesignationRequestDTO dto);

    DesignationResponseDTO getById(Long id);

    PageResponseDTO<DesignationResponseDTO> getAll(Pageable pageable);

    void delete(Long id);
}
