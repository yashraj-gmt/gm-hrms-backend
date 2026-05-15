package com.gm.hrms.service;

import com.gm.hrms.dto.request.BreakPolicyRequestDTO;
import com.gm.hrms.dto.response.BreakPolicyResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.enums.BreakCategory;
import org.springframework.data.domain.Pageable;

public interface BreakPolicyService {

    BreakPolicyResponseDTO create(BreakPolicyRequestDTO dto);

    BreakPolicyResponseDTO update(Long id, BreakPolicyRequestDTO dto);

    BreakPolicyResponseDTO getById(Long id);

    PageResponseDTO<BreakPolicyResponseDTO> getAll(
            String search, BreakCategory category, Boolean isPaid, Pageable pageable
    );

    /** Soft-delete: sets isActive = false */
    void delete(Long id);
}