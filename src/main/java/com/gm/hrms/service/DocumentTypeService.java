package com.gm.hrms.service;

import com.gm.hrms.dto.request.DocumentTypeRequestDTO;
import com.gm.hrms.dto.response.DocumentTypeResponseDTO;
import com.gm.hrms.dto.response.DocumentTypeStatsDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.enums.ApplicableType;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface DocumentTypeService {

    DocumentTypeResponseDTO create(DocumentTypeRequestDTO dto);
    DocumentTypeResponseDTO update(Long id, DocumentTypeRequestDTO dto);
    void delete(Long id);

    PageResponseDTO<DocumentTypeResponseDTO> getAll(Pageable pageable);

    DocumentTypeResponseDTO getById(Long id);

    PageResponseDTO<DocumentTypeResponseDTO> getByApplicableType(ApplicableType type, Pageable pageable);

    PageResponseDTO<DocumentTypeResponseDTO> getByApplicableTypes(Set<ApplicableType> types, Pageable pageable);

    DocumentTypeStatsDTO getStats();
}