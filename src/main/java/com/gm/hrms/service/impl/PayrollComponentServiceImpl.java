package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.PayrollComponentRequestDTO;
import com.gm.hrms.dto.response.PayrollComponentResponseDTO;
import com.gm.hrms.entity.PayrollComponent;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.PayrollComponentMapper;
import com.gm.hrms.repository.PayrollComponentRepository;
import com.gm.hrms.service.PayrollComponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollComponentServiceImpl implements PayrollComponentService {

    private final PayrollComponentRepository repository;

    @Override
    public PayrollComponentResponseDTO create(PayrollComponentRequestDTO dto) {
        if (repository.existsByCode(dto.getCode().toUpperCase())) {
            throw new DuplicateResourceException(
                    "Payroll component with code '" + dto.getCode().toUpperCase() + "' already exists"
            );
        }

        if (repository.existsByName(dto.getName())) {
            throw new DuplicateResourceException(
                    "Payroll component with name '" + dto.getName() + "' already exists"
            );
        }
        PayrollComponent entity = PayrollComponentMapper.toEntity(dto);
        return PayrollComponentMapper.toResponse(repository.save(entity));
    }

    @Override
    public PayrollComponentResponseDTO update(Long id, PayrollComponentRequestDTO dto) {
        PayrollComponent entity = findById(id);

        if (entity.getIsSystemDefined())
            throw new RuntimeException("System-defined components cannot be modified");

        PayrollComponentMapper.patchEntity(entity, dto);
        return PayrollComponentMapper.toResponse(repository.save(entity));
    }

    @Override
    public PayrollComponentResponseDTO getById(Long id) {
        return PayrollComponentMapper.toResponse(findById(id));
    }

    @Override
    public List<PayrollComponentResponseDTO> getAll() {
        return repository.findAllByIsActiveTrueOrderByDisplayOrderAsc()
                .stream().map(PayrollComponentMapper::toResponse).toList();
    }

    @Override
    public void delete(Long id) {
        PayrollComponent entity = findById(id);
        if (entity.getIsSystemDefined())
            throw new RuntimeException("System-defined components cannot be deleted");

        entity.setIsActive(false);
        repository.save(entity);
    }

    private PayrollComponent findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll component not found"));
    }
}