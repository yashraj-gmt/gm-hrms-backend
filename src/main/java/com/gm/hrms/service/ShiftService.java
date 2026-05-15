package com.gm.hrms.service;

import com.gm.hrms.dto.request.ShiftRequestDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.dto.response.ShiftResponseDTO;
import org.springframework.data.domain.Pageable;

public interface ShiftService {
    ShiftResponseDTO create(ShiftRequestDTO dto);
    ShiftResponseDTO update(Long id, ShiftRequestDTO dto);
    ShiftResponseDTO getById(Long id);
    PageResponseDTO<ShiftResponseDTO> getAll(Pageable pageable);
    ShiftResponseDTO toggleStatus(Long id);
    void delete(Long id);
}