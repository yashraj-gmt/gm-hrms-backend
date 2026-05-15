package com.gm.hrms.service;

import com.gm.hrms.dto.request.LeavePolicyRequestDTO;
import com.gm.hrms.dto.response.LeavePolicyResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import org.springframework.data.domain.Pageable;

public interface LeavePolicyService {

    LeavePolicyResponseDTO create(LeavePolicyRequestDTO dto);

    LeavePolicyResponseDTO getById(Long id);

    PageResponseDTO<LeavePolicyResponseDTO> getAll(Pageable pageable);

    LeavePolicyResponseDTO update(Long id, LeavePolicyRequestDTO dto);

    LeavePolicyResponseDTO patchUpdate(Long id, LeavePolicyRequestDTO dto);

    void delete(Long id);
}