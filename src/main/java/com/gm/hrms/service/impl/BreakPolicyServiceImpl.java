package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.BreakPolicyRequestDTO;
import com.gm.hrms.dto.response.BreakPolicyResponseDTO;
import com.gm.hrms.dto.response.BreakPolicyStatsDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.entity.BreakPolicy;
import com.gm.hrms.enums.BreakCategory;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.BreakPolicyMapper;
import com.gm.hrms.repository.BreakPolicyRepository;
import com.gm.hrms.service.BreakPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BreakPolicyServiceImpl implements BreakPolicyService {

    private final BreakPolicyRepository repository;

    // ── CREATE ────────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public BreakPolicyResponseDTO create(BreakPolicyRequestDTO dto) {

        // Only block duplicates against non-deleted records
        if (repository.existsByBreakNameAndIsDeletedFalse(dto.getBreakName())) {
            throw new DuplicateResourceException(
                    "Break policy already exists with name: " + dto.getBreakName());
        }

        BreakPolicy entity = BreakPolicyMapper.toEntity(dto);
        entity.setIsActive(true);
        entity.setIsDeleted(false);   // explicit for clarity

        return BreakPolicyMapper.toResponse(repository.save(entity));
    }

    // ── UPDATE (handles isActive toggle from Edit form) ───────────────────────
    // No change needed — patchEntity already handles isActive; isDeleted is never
    // exposed through the request DTO so it cannot be accidentally toggled here.
    @Override
    @Transactional
    public BreakPolicyResponseDTO update(Long id, BreakPolicyRequestDTO dto) {

        BreakPolicy entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Break policy not found with id: " + id));

        BreakPolicyMapper.patchEntity(entity, dto);

        return BreakPolicyMapper.toResponse(repository.save(entity));
    }

    // ── GET BY ID ─────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public BreakPolicyResponseDTO getById(Long id) {

        BreakPolicy entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Break policy not found with id: " + id));

        return BreakPolicyMapper.toResponse(entity);
    }

    // ── GET ALL ───────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<BreakPolicyResponseDTO> getAll(
            String search, BreakCategory category, Boolean isPaid, Pageable pageable) {

        String searchParam = (search == null || search.isBlank()) ? null : search.trim();

        Page<BreakPolicy> page = repository.search(searchParam, category, isPaid, pageable);

        List<BreakPolicyResponseDTO> content = page.getContent()
                .stream()
                .map(BreakPolicyMapper::toResponse)
                .toList();

        return PageResponseDTO.<BreakPolicyResponseDTO>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    // ── STATS ─────────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public BreakPolicyStatsDTO getStats() {
        long total    = repository.countByIsDeletedFalse();
        long active   = repository.countByIsDeletedFalseAndIsActiveTrue();
        long fixed    = repository.countByIsDeletedFalseAndBreakCategory(BreakCategory.FIXED);
        long flexible = repository.countByIsDeletedFalseAndBreakCategory(BreakCategory.FLEXIBLE);

        return BreakPolicyStatsDTO.builder()
                .total(total)
                .active(active)       // ← now correctly counts only isActive=true rows
                .fixed(fixed)
                .flexible(flexible)
                .build();
    }

    // ── SOFT DELETE ───────────────────────────────────────────────────────────
    @Override
    @Transactional
    public void delete(Long id) {

        BreakPolicy entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Break policy not found with id: " + id));

        entity.setIsDeleted(true);    // ← soft-delete flag; isActive is left unchanged
        repository.save(entity);
    }
}