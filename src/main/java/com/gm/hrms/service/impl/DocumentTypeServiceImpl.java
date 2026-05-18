package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.DocumentTypeRequestDTO;
import com.gm.hrms.dto.response.DocumentTypeResponseDTO;
import com.gm.hrms.dto.response.DocumentTypeStatsDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.entity.DocumentType;
import com.gm.hrms.enums.ApplicableType;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.DocumentTypeMapper;
import com.gm.hrms.repository.DocumentTypeRepository;
import com.gm.hrms.service.DocumentTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentTypeServiceImpl implements DocumentTypeService {

    private final DocumentTypeRepository repository;

    // ── CREATE ────────────────────────────────────────────────────────────────
    @Override
    public DocumentTypeResponseDTO create(DocumentTypeRequestDTO dto) {
        if (repository.existsByNameIgnoreCase(dto.getName()))
            throw new DuplicateResourceException("Document name already exists: " + dto.getName());
        if (repository.existsByDocKeyIgnoreCase(dto.getKey()))
            throw new DuplicateResourceException("Document key already exists: " + dto.getKey());

        DocumentType entity = DocumentTypeMapper.toEntity(dto);
        repository.save(entity);
        return DocumentTypeMapper.toResponse(entity);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    @Override
    public DocumentTypeResponseDTO update(Long id, DocumentTypeRequestDTO dto) {
        DocumentType entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document type not found: " + id));

        if (repository.existsByNameIgnoreCaseAndIdNot(dto.getName(), id))
            throw new DuplicateResourceException("Document name already exists: " + dto.getName());
        if (repository.existsByDocKeyIgnoreCaseAndIdNot(dto.getKey(), id))
            throw new DuplicateResourceException("Document key already exists: " + dto.getKey());

        DocumentTypeMapper.updateEntity(entity, dto);
        repository.save(entity);
        return DocumentTypeMapper.toResponse(entity);
    }

    // ── DELETE (true soft-delete) ─────────────────────────────────────────────
    // Sets deleted=true + deletedAt. Record disappears from listing
    // but remains in the DB. @SQLRestriction on the entity auto-hides it.
    @Override
    public void delete(Long id) {
        DocumentType entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document type not found: " + id));
        entity.setDeleted(true);
        entity.setDeletedAt(LocalDateTime.now());
        repository.save(entity);
    }

    // ── GET ALL (non-deleted, active + inactive) ───────────────────────────────
    // @SQLRestriction("deleted = false") on the entity auto-filters this query.
    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<DocumentTypeResponseDTO> getAll(Pageable pageable) {
        return buildPageResponse(repository.findAll(pageable));
    }

    // ── GET BY ID ─────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public DocumentTypeResponseDTO getById(Long id) {
        DocumentType entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document type not found: " + id));
        return DocumentTypeMapper.toResponse(entity);
    }

    // ── FILTER BY SINGLE TYPE ─────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<DocumentTypeResponseDTO> getByApplicableType(
            ApplicableType type, Pageable pageable) {
        return buildPageResponse(repository.findByApplicableType(type, pageable));
    }

    // ── FILTER BY MULTIPLE TYPES ──────────────────────────────────────────────
    // Returns docs matching ANY of the given types (OR logic).
    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<DocumentTypeResponseDTO> getByApplicableTypes(
            Set<ApplicableType> types, Pageable pageable) {
        return buildPageResponse(repository.findByApplicableTypesIn(types, pageable));
    }

    // ── STATS (non-deleted only, auto-filtered by @SQLRestriction) ────────────
    @Override
    @Transactional(readOnly = true)
    public DocumentTypeStatsDTO getStats() {
        long active   = repository.countByActiveTrue();
        long inactive = repository.countByActiveFalse();
        return DocumentTypeStatsDTO.builder()
                .total(active + inactive)
                .active(active)
                .inactive(inactive)
                .build();
    }

    // ── Private helper ────────────────────────────────────────────────────────
    private PageResponseDTO<DocumentTypeResponseDTO> buildPageResponse(Page<DocumentType> page) {
        List<DocumentTypeResponseDTO> content = page.getContent()
                .stream()
                .map(DocumentTypeMapper::toResponse)
                .toList();
        return PageResponseDTO.<DocumentTypeResponseDTO>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}