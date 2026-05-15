package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.DepartmentRequestDTO;
import com.gm.hrms.dto.response.DepartmentResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.entity.Department;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.DepartmentMapper;
import com.gm.hrms.repository.DepartmentRepository;
import com.gm.hrms.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository repository;

    // ── CREATE ────────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public DepartmentResponseDTO createDepartment(DepartmentRequestDTO dto) {

        if (repository.existsByName(dto.getName()))
            throw new DuplicateResourceException("Department already exists with name: " + dto.getName());

        if (repository.existsByCode(dto.getCode()))
            throw new DuplicateResourceException("Department already exists with code: " + dto.getCode());

        Department dept = DepartmentMapper.toEntity(dto);
        return DepartmentMapper.toResponse(repository.save(dept));
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public DepartmentResponseDTO updateDepartment(Long id, DepartmentRequestDTO dto) {

        Department dept = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));

        if (dto.getName() != null
                && !dept.getName().equalsIgnoreCase(dto.getName())
                && repository.existsByNameAndIdNot(dto.getName(), id))
            throw new DuplicateResourceException("Department already exists with name: " + dto.getName());

        if (dto.getCode() != null
                && !dept.getCode().equalsIgnoreCase(dto.getCode())
                && repository.existsByCodeAndIdNot(dto.getCode(), id))
            throw new DuplicateResourceException("Department already exists with code: " + dto.getCode());

        DepartmentMapper.patchUpdate(dept, dto);
        return DepartmentMapper.toResponse(repository.save(dept));
    }

    // ── GET BY ID ─────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public DepartmentResponseDTO getDepartmentById(Long id) {
        Department dept = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        return DepartmentMapper.toResponse(dept);
    }

    // ── GET ALL ───────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<DepartmentResponseDTO> getAllDepartments(
            String search, Boolean status, Pageable pageable) {

        String searchParam = (search == null || search.isBlank()) ? null : search.trim();

        Page<Department> page = repository.searchDepartments(searchParam, status, pageable);

        List<DepartmentResponseDTO> content = page.getContent()
                .stream()
                .map(DepartmentMapper::toResponse)
                .toList();

        // fix #2 – real counts across ALL pages (SQLRestriction filters deleted=false)
        long totalActive   = repository.countByStatus(true);
        long totalInactive = repository.countByStatus(false);

        return PageResponseDTO.<DepartmentResponseDTO>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .totalActive(totalActive)
                .totalInactive(totalInactive)
                .build();
    }

    // ── SOFT DELETE (fix #6 + fix #9) ────────────────────────────────────────
    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        Department dept = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));

        dept.setDeleted(true);

        dept.getSubDepartments().forEach(sub -> sub.setDeleted(true));

        repository.save(dept);
    }
}