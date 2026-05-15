package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.CompOffRuleRequestDTO;
import com.gm.hrms.dto.response.CompOffRuleResponseDTO;
import com.gm.hrms.entity.CompOffRule;
import com.gm.hrms.entity.LeavePolicy;
import com.gm.hrms.exception.*;
import com.gm.hrms.mapper.CompOffRuleMapper;
import com.gm.hrms.repository.CompOffRuleRepository;
import com.gm.hrms.repository.LeavePolicyRepository;
import com.gm.hrms.service.CompOffRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompOffRuleServiceImpl implements CompOffRuleService {

    private final CompOffRuleRepository repository;
    private final LeavePolicyRepository policyRepository;

    @Override
    public CompOffRuleResponseDTO create(CompOffRuleRequestDTO dto) {

        LeavePolicy policy = policyRepository.findById(dto.getPolicyId())
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));

        if (repository.existsByLeavePolicyId(dto.getPolicyId())) {
            throw new DuplicateResourceException("Rule already exists for this policy");
        }

        CompOffRule entity = CompOffRuleMapper.toEntity(dto, policy);

        return CompOffRuleMapper.toResponse(repository.save(entity));
    }

    @Override
    public CompOffRuleResponseDTO getByPolicy(Long policyId) {

        CompOffRule entity = repository.findByLeavePolicyIdAndIsActiveTrue(policyId)
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found"));

        return CompOffRuleMapper.toResponse(entity);
    }

    @Override
    public CompOffRuleResponseDTO update(Long id, CompOffRuleRequestDTO dto) {

        CompOffRule entity = get(id);

        if (entity.getIsSystemDefined()) {
            throw new InvalidRequestException("System rule cannot be modified");
        }

        CompOffRuleMapper.updateEntity(entity, dto);

        return CompOffRuleMapper.toResponse(repository.save(entity));
    }

    @Override
    public CompOffRuleResponseDTO patchUpdate(Long id, CompOffRuleRequestDTO dto) {

        return update(id, dto);
    }

    @Override
    public void delete(Long id) {

        CompOffRule entity = get(id);

        if (entity.getIsSystemDefined()) {
            throw new InvalidRequestException("System rule cannot be deleted");
        }

        entity.setIsActive(false);

        repository.save(entity);
    }

    private CompOffRule get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found"));
    }
}