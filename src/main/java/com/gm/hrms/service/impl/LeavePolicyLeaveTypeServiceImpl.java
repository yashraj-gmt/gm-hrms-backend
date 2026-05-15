package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.LeavePolicyLeaveTypeRequestDTO;
import com.gm.hrms.dto.response.LeavePolicyLeaveTypeResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.entity.LeavePolicy;
import com.gm.hrms.entity.LeavePolicyLeaveType;
import com.gm.hrms.entity.LeaveType;
import com.gm.hrms.enums.AccrualType;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.LeavePolicyLeaveTypeMapper;
import com.gm.hrms.repository.LeavePolicyLeaveTypeRepository;
import com.gm.hrms.repository.LeavePolicyRepository;
import com.gm.hrms.repository.LeaveTypeRepository;
import com.gm.hrms.service.LeavePolicyLeaveTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LeavePolicyLeaveTypeServiceImpl implements LeavePolicyLeaveTypeService {

    private final LeavePolicyLeaveTypeRepository repository;
    private final LeavePolicyRepository policyRepository;
    private final LeaveTypeRepository leaveTypeRepository;

    // ================= CREATE =================
    @Override
    @Transactional
    public LeavePolicyLeaveTypeResponseDTO create(LeavePolicyLeaveTypeRequestDTO dto) {

        LeavePolicy policy = policyRepository.findById(dto.getPolicyId())
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));

        LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found"));

        if (repository.existsByLeavePolicyIdAndLeaveTypeIdAndIsActiveTrue(
                dto.getPolicyId(), dto.getLeaveTypeId())) {
            throw new DuplicateResourceException("Mapping already exists");
        }

        validateAccrual(dto.getTotalLeaves(), dto.getAccrualType(), dto.getAccrualValue());

        LeavePolicyLeaveType entity = LeavePolicyLeaveType.builder()
                .leavePolicy(policy)
                .leaveType(leaveType)
                .totalLeaves(dto.getTotalLeaves())
                .accrualType(dto.getAccrualType())
                .accrualValue(dto.getAccrualValue())
                .isActive(true)
                .build();

        return LeavePolicyLeaveTypeMapper.toResponse(repository.save(entity));
    }

    // ================= GET =================
    @Override
    public LeavePolicyLeaveTypeResponseDTO getById(Long id) {

        LeavePolicyLeaveType entity = repository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mapping not found"));

        return LeavePolicyLeaveTypeMapper.toResponse(entity);
    }

    // ================= GET ALL =================
    @Override
    public PageResponseDTO<LeavePolicyLeaveTypeResponseDTO> getAll(Pageable pageable) {

        Page<LeavePolicyLeaveType> page = repository.findByIsActiveTrue(pageable);

        return PageResponseDTO.<LeavePolicyLeaveTypeResponseDTO>builder()
                .content(page.map(LeavePolicyLeaveTypeMapper::toResponse).getContent())
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
    public LeavePolicyLeaveTypeResponseDTO patchUpdate(Long id, LeavePolicyLeaveTypeRequestDTO dto) {

        LeavePolicyLeaveType entity = repository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mapping not found"));

        if (dto.getTotalLeaves() == null &&
                dto.getAccrualType() == null &&
                dto.getAccrualValue() == null) {

            throw new InvalidRequestException("At least one field required");
        }

        validateAccrual(
                dto.getTotalLeaves() != null ? dto.getTotalLeaves() : entity.getTotalLeaves(),
                dto.getAccrualType() != null ? dto.getAccrualType() : entity.getAccrualType(),
                dto.getAccrualValue() != null ? dto.getAccrualValue() : entity.getAccrualValue()
        );

        LeavePolicyLeaveTypeMapper.updateEntity(
                entity,
                dto.getTotalLeaves(),
                dto.getAccrualType(),
                dto.getAccrualValue()
        );

        return LeavePolicyLeaveTypeMapper.toResponse(repository.save(entity));
    }

    // ================= DELETE =================
    @Override
    @Transactional
    public void delete(Long id) {

        LeavePolicyLeaveType entity = repository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mapping not found"));

        entity.setIsActive(false);

        repository.save(entity);
    }

    // ================= VALIDATION =================
    private void validateAccrual(Integer total, AccrualType type, Integer value) {

        if (total == null || total < 0) {
            throw new InvalidRequestException("Invalid total leaves");
        }

        if (type == null) {
            throw new InvalidRequestException("Accrual type required");
        }

        if (type == AccrualType.MONTHLY) {
            if (value == null || value <= 0) {
                throw new InvalidRequestException("Invalid accrual value");
            }
            if (value > total) {
                throw new InvalidRequestException("Accrual value cannot exceed total leaves");
            }
        }
    }
}