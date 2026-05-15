package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.LeaveEligibilityRuleRequestDTO;
import com.gm.hrms.dto.response.LeaveEligibilityRuleResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.entity.LeaveEligibilityRule;
import com.gm.hrms.entity.LeavePolicy;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.LeaveEligibilityRuleMapper;
import com.gm.hrms.repository.LeaveEligibilityRuleRepository;
import com.gm.hrms.repository.LeavePolicyRepository;
import com.gm.hrms.service.LeaveEligibilityRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveEligibilityRuleServiceImpl implements LeaveEligibilityRuleService {

    private final LeaveEligibilityRuleRepository repository;
    private final LeavePolicyRepository policyRepository;

    // ================= CREATE =================
    @Override
    @Transactional
    public LeaveEligibilityRuleResponseDTO create(LeaveEligibilityRuleRequestDTO dto) {

        LeavePolicy policy = policyRepository.findById(dto.getPolicyId())
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));

        if (dto.getProbationPeriodInMonths() == null) {
            throw new InvalidRequestException("Probation period is required");
        }

        if (repository.existsByLeavePolicyIdAndIsActiveTrue(dto.getPolicyId())) {
            throw new DuplicateResourceException("Eligibility rule already exists");
        }

        validateCreate(dto);

        LeaveEligibilityRule entity = LeaveEligibilityRuleMapper.toEntity(dto, policy);

        return LeaveEligibilityRuleMapper.toResponse(repository.save(entity));
    }

    // ================= GET =================
    @Override
    public LeaveEligibilityRuleResponseDTO getByPolicy(Long policyId) {

        LeaveEligibilityRule entity = repository.findByLeavePolicyIdAndIsActiveTrue(policyId)
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found"));

        return LeaveEligibilityRuleMapper.toResponse(entity);
    }

    // ================= PATCH =================
    @Override
    @Transactional
    public LeaveEligibilityRuleResponseDTO update(Long id, LeaveEligibilityRuleRequestDTO dto) {

        LeaveEligibilityRule entity = repository.findById(id)
                .filter(e -> Boolean.TRUE.equals(e.getIsActive()))
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found"));

        if (Boolean.TRUE.equals(entity.getIsSystemDefined())) {
            throw new InvalidRequestException("System rule cannot be modified");
        }

        if (isEmpty(dto)) {
            throw new InvalidRequestException("At least one field required for update");
        }

        LeaveEligibilityRuleMapper.patchUpdate(entity, dto);

        validateAfterPatch(entity);

        return LeaveEligibilityRuleMapper.toResponse(repository.save(entity));
    }

    // ================= DELETE =================
    @Override
    @Transactional
    public void delete(Long id) {

        LeaveEligibilityRule entity = repository.findById(id)
                .filter(e -> Boolean.TRUE.equals(e.getIsActive()))
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found"));

        if (Boolean.TRUE.equals(entity.getIsSystemDefined())) {
            throw new InvalidRequestException("System rule cannot be deleted");
        }

        entity.setIsActive(false);

        repository.save(entity);
    }

    // ================= VALIDATION =================

    private void validateCreate(LeaveEligibilityRuleRequestDTO dto) {

        if (dto.getProbationPeriodInMonths() == null || dto.getProbationPeriodInMonths() < 0) {
            throw new InvalidRequestException("Invalid probation period");
        }

        if (dto.getAllowCompOff() == null) {
            throw new InvalidRequestException("CompOff flag is required");
        }
    }
    private void validateAfterPatch(LeaveEligibilityRule entity) {

        if (entity.getProbationPeriodInMonths() == null || entity.getProbationPeriodInMonths() < 0) {
            throw new InvalidRequestException("Invalid probation period");
        }
    }

    private boolean isEmpty(LeaveEligibilityRuleRequestDTO dto) {
        return dto.getProbationPeriodInMonths() == null &&
                dto.getAllowCompOff() == null;
    }

    @Override
    public PageResponseDTO<LeaveEligibilityRuleResponseDTO> getAll(Pageable pageable) {

        Page<LeaveEligibilityRule> page =
                repository.findByIsActiveTrue(pageable);

        List<LeaveEligibilityRuleResponseDTO> content = page.getContent()
                .stream()
                .map(LeaveEligibilityRuleMapper::toResponse)
                .toList();

        return PageResponseDTO.<LeaveEligibilityRuleResponseDTO>builder()
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