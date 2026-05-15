package com.gm.hrms.service.impl;

import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.dto.request.LeavePolicyRequestDTO;
import com.gm.hrms.dto.response.LeavePolicyResponseDTO;
import com.gm.hrms.entity.LeavePolicy;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.LeavePolicyMapper;
import com.gm.hrms.repository.LeavePolicyRepository;
import com.gm.hrms.service.LeavePolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class LeavePolicyServiceImpl implements LeavePolicyService {

    private final LeavePolicyRepository repository;

    // ================= CREATE =================
    @Override
    public LeavePolicyResponseDTO create(LeavePolicyRequestDTO dto) {

        validateDates(dto.getEffectiveFrom(), dto.getEffectiveTo());

        // 🔥 One active policy per employment type
        if (repository.existsByEmploymentTypeAndIsActiveTrue(dto.getEmploymentType())) {
            throw new DuplicateResourceException(
                    "Policy already exists for this employment type"
            );
        }

        LeavePolicy entity = LeavePolicyMapper.toEntity(dto);

        return LeavePolicyMapper.toResponse(repository.save(entity));
    }

    // ================= GET BY ID =================
    @Override
    public LeavePolicyResponseDTO getById(Long id) {

        LeavePolicy entity = getEntity(id);

        return LeavePolicyMapper.toResponse(entity);
    }

    // ================= GET ALL =================
    @Override
    public PageResponseDTO<LeavePolicyResponseDTO> getAll(Pageable pageable) {

        Page<LeavePolicy> page = repository.findAll(pageable);

        return PageResponseDTO.<LeavePolicyResponseDTO>builder()
                .content(page.getContent()
                        .stream()
                        .filter(LeavePolicy::getIsActive)   // ✅ only active
                        .map(LeavePolicyMapper::toResponse)
                        .toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    // ================= PUT (FULL UPDATE) =================
    @Override
    public LeavePolicyResponseDTO update(Long id, LeavePolicyRequestDTO dto) {

        LeavePolicy entity = getEntity(id);

        if (entity.getIsSystemDefined()) {
            throw new InvalidRequestException("System policy cannot be modified");
        }

        validateDates(dto.getEffectiveFrom(), dto.getEffectiveTo());

        // 🔥 Prevent duplicate on employment type change
        if (!entity.getEmploymentType().equals(dto.getEmploymentType())
                && repository.existsByEmploymentTypeAndIsActiveTrue(dto.getEmploymentType())) {

            throw new DuplicateResourceException(
                    "Policy already exists for this employment type"
            );
        }

        entity.setPolicyName(dto.getPolicyName());
        entity.setDescription(dto.getDescription());
        entity.setEmploymentType(dto.getEmploymentType());
        entity.setEffectiveFrom(dto.getEffectiveFrom());
        entity.setEffectiveTo(dto.getEffectiveTo());

        entity.setRequiresApproval(dto.getRequiresApproval());
        entity.setAllowHalfDay(dto.getAllowHalfDay());
        entity.setAllowBackdatedLeave(dto.getAllowBackdatedLeave());
        entity.setSandwichRuleEnabled(dto.getSandwichRuleEnabled());

        return LeavePolicyMapper.toResponse(repository.save(entity));
    }

    // ================= PATCH (PARTIAL UPDATE) =================
    @Override
    public LeavePolicyResponseDTO patchUpdate(Long id, LeavePolicyRequestDTO dto) {

        LeavePolicy entity = getEntity(id);

        if (entity.getIsSystemDefined()) {
            throw new InvalidRequestException("System policy cannot be modified");
        }

        // 🔥 Prevent empty PATCH
        if (isEmptyPatch(dto)) {
            throw new InvalidRequestException("At least one field must be provided");
        }

        // 🔥 Duplicate check if employment type changed
        if (dto.getEmploymentType() != null &&
                !entity.getEmploymentType().equals(dto.getEmploymentType()) &&
                repository.existsByEmploymentTypeAndIsActiveTrue(dto.getEmploymentType())) {

            throw new DuplicateResourceException(
                    "Policy already exists for this employment type"
            );
        }

        LeavePolicyMapper.updateEntity(entity, dto);

        validateDates(entity.getEffectiveFrom(), entity.getEffectiveTo());

        return LeavePolicyMapper.toResponse(repository.save(entity));
    }

    // ================= DELETE (SOFT) =================
    @Override
    public void delete(Long id) {

        LeavePolicy entity = getEntity(id);

        if (entity.getIsSystemDefined()) {
            throw new InvalidRequestException("System policy cannot be deleted");
        }

        entity.setIsActive(false);
        entity.setDeletedAt(LocalDate.now());

        repository.save(entity);
    }

    // ================= COMMON METHODS =================

    private LeavePolicy getEntity(Long id) {
        return repository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));
    }

    private void validateDates(LocalDate from, LocalDate to) {
        if (from == null) {
            throw new InvalidRequestException("Effective from date is required");
        }
        if (to != null && to.isBefore(from)) {
            throw new InvalidRequestException("Invalid date range");
        }
    }

    private boolean isEmptyPatch(LeavePolicyRequestDTO dto) {
        return dto.getPolicyName() == null &&
                dto.getDescription() == null &&
                dto.getEmploymentType() == null &&
                dto.getEffectiveFrom() == null &&
                dto.getEffectiveTo() == null &&
                dto.getRequiresApproval() == null &&
                dto.getAllowHalfDay() == null &&
                dto.getAllowBackdatedLeave() == null &&
                dto.getSandwichRuleEnabled() == null;
    }
}