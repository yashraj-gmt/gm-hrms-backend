package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.LeaveApplicationRuleRequestDTO;
import com.gm.hrms.dto.response.LeaveApplicationRuleResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.entity.LeaveApplicationRule;
import com.gm.hrms.entity.LeavePolicy;
import com.gm.hrms.exception.*;
import com.gm.hrms.mapper.LeaveApplicationRuleMapper;
import com.gm.hrms.repository.LeaveApplicationRuleRepository;
import com.gm.hrms.repository.LeavePolicyRepository;
import com.gm.hrms.service.LeaveApplicationRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveApplicationRuleServiceImpl implements LeaveApplicationRuleService {

    private final LeaveApplicationRuleRepository repository;
    private final LeavePolicyRepository policyRepository;

    // ================= CREATE =================
    @Override
    @Transactional
    public LeaveApplicationRuleResponseDTO create(LeaveApplicationRuleRequestDTO dto) {

        LeavePolicy policy = policyRepository.findById(dto.getPolicyId())
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));

        if (repository.existsByLeavePolicyIdAndIsActiveTrue(dto.getPolicyId())) {
            throw new DuplicateResourceException("Rule already exists for this policy");
        }

        validateCreate(dto);

        LeaveApplicationRule entity = LeaveApplicationRuleMapper.toEntity(dto, policy);

        validateBusiness(entity);

        return LeaveApplicationRuleMapper.toResponse(repository.save(entity));
    }

    // ================= GET BY POLICY =================
    @Override
    public LeaveApplicationRuleResponseDTO getByPolicy(Long policyId) {

        LeaveApplicationRule entity = repository.findByLeavePolicyIdAndIsActiveTrue(policyId)
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found"));

        return LeaveApplicationRuleMapper.toResponse(entity);
    }

    // ================= GET ALL =================
    @Override
    public PageResponseDTO<LeaveApplicationRuleResponseDTO> getAll(Pageable pageable) {

        Page<LeaveApplicationRule> page =
                repository.findByIsActiveTrue(pageable);

        List<LeaveApplicationRuleResponseDTO> content = page.getContent()
                .stream()
                .map(LeaveApplicationRuleMapper::toResponse)
                .toList();

        return PageResponseDTO.<LeaveApplicationRuleResponseDTO>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    // ================= PATCH =================
    @Override
    @Transactional
    public LeaveApplicationRuleResponseDTO patchUpdate(Long id, LeaveApplicationRuleRequestDTO dto) {

        LeaveApplicationRule entity = repository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found"));

        if (Boolean.TRUE.equals(entity.getIsSystemDefined())) {
            throw new InvalidRequestException("System rule cannot be modified");
        }

        if (isEmpty(dto)) {
            throw new InvalidRequestException("At least one field required");
        }

        LeaveApplicationRuleMapper.patchUpdate(entity, dto);

        validateBusiness(entity);

        return LeaveApplicationRuleMapper.toResponse(repository.save(entity));
    }

    // ================= DELETE =================
    @Override
    @Transactional
    public void delete(Long id) {

        LeaveApplicationRule entity = repository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found"));

        if (Boolean.TRUE.equals(entity.getIsSystemDefined())) {
            throw new InvalidRequestException("System rule cannot be deleted");
        }

        entity.setIsActive(false);

        repository.save(entity);
    }

    // ================= VALIDATION =================

    private void validateCreate(LeaveApplicationRuleRequestDTO dto) {

        if (dto.getAllowHalfDay() == null ||
                dto.getMinLeaveDuration() == null ||
                dto.getMaxConsecutiveDays() == null ||
                dto.getApplyBeforeDays() == null ||
                dto.getAllowBackdatedLeave() == null ||
                dto.getSandwichRuleEnabled() == null) {

            throw new InvalidRequestException("All required fields must be provided");
        }
    }

    private void validateBusiness(LeaveApplicationRule entity) {

        // Sandwich rule validation
        if (Boolean.TRUE.equals(entity.getSandwichRuleEnabled())) {

            if (entity.getIncludeHolidays() == null || entity.getIncludeWeekends() == null) {
                throw new InvalidRequestException(
                        "Include holidays and weekends must be defined when sandwich rule is enabled"
                );
            }
        }

        // Half day validation
        if (Boolean.FALSE.equals(entity.getAllowHalfDay())
                && entity.getMinLeaveDuration() < 1) {

            throw new InvalidRequestException(
                    "Half day not allowed, min leave duration cannot be less than 1"
            );
        }
    }

    private boolean isEmpty(LeaveApplicationRuleRequestDTO dto) {

        return dto.getAllowHalfDay() == null &&
                dto.getMinLeaveDuration() == null &&
                dto.getMaxConsecutiveDays() == null &&
                dto.getApplyBeforeDays() == null &&
                dto.getAllowBackdatedLeave() == null &&
                dto.getSandwichRuleEnabled() == null &&
                dto.getIncludeHolidays() == null &&
                dto.getIncludeWeekends() == null;
    }
}