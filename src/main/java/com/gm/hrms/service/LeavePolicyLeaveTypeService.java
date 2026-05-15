package com.gm.hrms.service;

import com.gm.hrms.dto.request.LeavePolicyLeaveTypeRequestDTO;
import com.gm.hrms.dto.response.LeavePolicyLeaveTypeResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import org.springframework.data.domain.Pageable;

public interface LeavePolicyLeaveTypeService {

    LeavePolicyLeaveTypeResponseDTO create(LeavePolicyLeaveTypeRequestDTO dto);

    LeavePolicyLeaveTypeResponseDTO getById(Long id);

    PageResponseDTO<LeavePolicyLeaveTypeResponseDTO> getAll(Pageable pageable);

    LeavePolicyLeaveTypeResponseDTO patchUpdate(Long id, LeavePolicyLeaveTypeRequestDTO dto);

    void delete(Long id);
}