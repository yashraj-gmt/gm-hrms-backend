package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.DesignationRequestDTO;
import com.gm.hrms.dto.response.DesignationResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.entity.Designation;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.DesignationMapper;
import com.gm.hrms.repository.DesignationRepository;
import com.gm.hrms.service.DesignationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DesignationServiceImpl implements DesignationService {

    private final DesignationRepository repository;

    // ================= CREATE =================

    @Override
    public DesignationResponseDTO create(DesignationRequestDTO dto) {

        // Duplicate check only among non-deleted rows
        if (repository.existsByNameIgnoreCaseAndDeletedFalse(dto.getName())) {
            throw new DuplicateResourceException(
                    "Designation already exists with name: " + dto.getName());
        }

        Designation entity = DesignationMapper.toEntity(dto);
        // deleted defaults to false via @Builder.Default / column default

        repository.save(entity);

        return DesignationMapper.toResponse(entity);
    }

    // ================= UPDATE =================

    @Override
    public DesignationResponseDTO update(Long id, DesignationRequestDTO dto) {

        // Only non-deleted records are editable
        Designation entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Designation not found with id: " + id));

        // Duplicate-name check only if the name is actually changing
        if (dto.getName() != null
                && !entity.getName().equalsIgnoreCase(dto.getName())
                && repository.existsByNameIgnoreCaseAndDeletedFalse(dto.getName())) {

            throw new DuplicateResourceException(
                    "Designation already exists with name: " + dto.getName());
        }

        DesignationMapper.updateEntity(entity, dto);

        repository.save(entity);

        return DesignationMapper.toResponse(entity);
    }

    // ================= GET BY ID =================

    @Override
    public DesignationResponseDTO getById(Long id) {

        // Deleted records are invisible
        Designation entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Designation not found with id: " + id));

        return DesignationMapper.toResponse(entity);
    }

    // ================= GET ALL =================

    @Override
    public PageResponseDTO<DesignationResponseDTO> getAll(Pageable pageable) {

        // Only return non-deleted rows
        Page<Designation> page = repository.findByDeletedFalse(pageable);

        List<DesignationResponseDTO> content = page.getContent()
                .stream()
                .map(DesignationMapper::toResponse)
                .toList();

        return PageResponseDTO.<DesignationResponseDTO>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    // ================= DELETE (soft — sets deleted = true) =================

    @Override
    public void delete(Long id) {

        Designation entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Designation not found with id: " + id));
        entity.setDeleted(true);

        repository.save(entity);
    }
}