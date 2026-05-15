package com.gm.hrms.service;

import com.gm.hrms.dto.request.LeaveTypeRequestDTO;
import com.gm.hrms.dto.response.LeaveTypeResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LeaveTypeService {

    LeaveTypeResponseDTO create(LeaveTypeRequestDTO dto);

    LeaveTypeResponseDTO getById(Long id);

    PageResponseDTO<LeaveTypeResponseDTO> getAll(String search, Pageable pageable);

    LeaveTypeResponseDTO update(Long id, LeaveTypeRequestDTO dto);

    void delete(Long id);
}