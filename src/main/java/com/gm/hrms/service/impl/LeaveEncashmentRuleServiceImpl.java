package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.LeaveEncashmentRuleRequestDTO;
import com.gm.hrms.dto.response.LeaveEncashmentRuleResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.entity.LeaveEncashmentRule;
import com.gm.hrms.entity.LeavePolicy;
import com.gm.hrms.exception.*;
import com.gm.hrms.mapper.LeaveEncashmentRuleMapper;
import com.gm.hrms.repository.LeaveEncashmentRuleRepository;
import com.gm.hrms.repository.LeavePolicyRepository;
import com.gm.hrms.service.LeaveEncashmentRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeaveEncashmentRuleServiceImpl implements LeaveEncashmentRuleService {

    private final LeaveEncashmentRuleRepository repository;
    private final LeavePolicyRepository policyRepository;

    @Override
    public LeaveEncashmentRuleResponseDTO create(LeaveEncashmentRuleRequestDTO dto) {

        LeavePolicy policy = policyRepository.findById(dto.getPolicyId())
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));

        if (repository.existsByLeavePolicyId(dto.getPolicyId())) {
            throw new DuplicateResourceException("Encashment rule already exists");
        }

        LeaveEncashmentRule entity =
                LeaveEncashmentRuleMapper.toEntity(dto, policy);

        return LeaveEncashmentRuleMapper.toResponse(repository.save(entity));
    }

    @Override
    public LeaveEncashmentRuleResponseDTO getByPolicy(Long policyId) {

        LeaveEncashmentRule entity =
                repository.findByLeavePolicyIdAndIsActiveTrue(policyId)
                        .orElseThrow(() -> new ResourceNotFoundException("Rule not found"));

        return LeaveEncashmentRuleMapper.toResponse(entity);
    }

    @Override
    public LeaveEncashmentRuleResponseDTO patchUpdate(
            Long id,
            LeaveEncashmentRuleRequestDTO dto
    ) {

        LeaveEncashmentRule entity = get(id);

        if (entity.getIsSystemDefined()) {
            throw new InvalidRequestException("System rule cannot be modified");
        }

        LeaveEncashmentRuleMapper.updateEntity(entity, dto);

        return LeaveEncashmentRuleMapper.toResponse(repository.save(entity));
    }

    @Override
    public void delete(Long id) {

        LeaveEncashmentRule entity = get(id);

        if (entity.getIsSystemDefined()) {
            throw new InvalidRequestException("System rule cannot be deleted");
        }

        entity.setIsActive(false);

        repository.save(entity);
    }

    private LeaveEncashmentRule get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found"));
    }

    @Override
    public PageResponseDTO<LeaveEncashmentRuleResponseDTO> getAll(Pageable pageable) {

        Page<LeaveEncashmentRule> page = repository.findByIsActiveTrue(pageable);

        return PageResponseDTO.<LeaveEncashmentRuleResponseDTO>builder()
                .content(page.getContent()
                        .stream()
                        .map(LeaveEncashmentRuleMapper::toResponse)
                        .toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}