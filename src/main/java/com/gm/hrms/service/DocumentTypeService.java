package com.gm.hrms.service;

import com.gm.hrms.dto.request.DocumentTypeRequestDTO;
import com.gm.hrms.dto.response.DocumentTypeResponseDTO;
import com.gm.hrms.dto.response.DocumentTypeStatsDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.enums.ApplicableType;
import org.springframework.data.domain.Pageable;

public interface DocumentTypeService {

    DocumentTypeResponseDTO create(DocumentTypeRequestDTO dto);

    DocumentTypeResponseDTO update(Long id, DocumentTypeRequestDTO dto);

    // Soft-delete: sets active = false, record stays in DB and listing
    void delete(Long id);

    // Returns ALL documents (active + inactive)
    PageResponseDTO<DocumentTypeResponseDTO> getAll(Pageable pageable);

    DocumentTypeResponseDTO getById(Long id);

    // Filter by applicableType — returns ALL (active + inactive)
    PageResponseDTO<DocumentTypeResponseDTO> getByApplicableType(ApplicableType type, Pageable pageable);

    // Counts for dashboard stats cards
    DocumentTypeStatsDTO getStats();
}