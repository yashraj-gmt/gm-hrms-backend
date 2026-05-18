package com.gm.hrms.service;

import com.gm.hrms.dto.request.BreakPolicyRequestDTO;
import com.gm.hrms.dto.response.BreakPolicyResponseDTO;
import com.gm.hrms.dto.response.BreakPolicyStatsDTO;
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

    /**
     * Returns global counts for active policies, broken down by category.
     * Intentionally ignores search / filter params so stat cards stay consistent.
     */
    BreakPolicyStatsDTO getStats();

    /**
     * Soft-delete: sets isActive = false.
     * Record disappears from the active listing but data is retained in the database.
     */
    void delete(Long id);
}