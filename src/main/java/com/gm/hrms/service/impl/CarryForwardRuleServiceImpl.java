package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.CarryForwardRuleRequestDTO;
import com.gm.hrms.dto.response.CarryForwardRuleResponseDTO;
import com.gm.hrms.entity.CarryForwardRule;
import com.gm.hrms.entity.LeavePolicy;
import com.gm.hrms.exception.*;
import com.gm.hrms.mapper.CarryForwardRuleMapper;
import com.gm.hrms.repository.CarryForwardRuleRepository;
import com.gm.hrms.repository.LeavePolicyRepository;
import com.gm.hrms.service.CarryForwardRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarryForwardRuleServiceImpl implements CarryForwardRuleService {

    private final CarryForwardRuleRepository repository;
    private final LeavePolicyRepository policyRepository;

    @Override
    public CarryForwardRuleResponseDTO create(CarryForwardRuleRequestDTO dto) {

        LeavePolicy policy = policyRepository.findById(dto.getPolicyId())
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));

        if (repository.existsByLeavePolicyId(dto.getPolicyId())) {
            throw new DuplicateResourceException("Carry forward rule already exists");
        }

        CarryForwardRule entity = CarryForwardRuleMapper.toEntity(dto, policy);

        return CarryForwardRuleMapper.toResponse(repository.save(entity));
    }

    @Override
    public CarryForwardRuleResponseDTO getByPolicy(Long policyId) {

        CarryForwardRule entity = repository.findByLeavePolicyIdAndIsActiveTrue(policyId)
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found"));

        return CarryForwardRuleMapper.toResponse(entity);
    }

    @Override
    public CarryForwardRuleResponseDTO patchUpdate(Long id, CarryForwardRuleRequestDTO dto) {

        CarryForwardRule entity = get(id);

        if (entity.getIsSystemDefined()) {
            throw new InvalidRequestException("System rule cannot be modified");
        }

        CarryForwardRuleMapper.updateEntity(entity, dto);

        return CarryForwardRuleMapper.toResponse(repository.save(entity));
    }

    @Override
    public void delete(Long id) {

        CarryForwardRule entity = get(id);

        if (entity.getIsSystemDefined()) {
            throw new InvalidRequestException("System rule cannot be deleted");
        }

        entity.setIsActive(false);

        repository.save(entity);
    }

    private CarryForwardRule get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found"));
    }
}