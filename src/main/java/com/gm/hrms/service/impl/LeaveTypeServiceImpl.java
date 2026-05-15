package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.LeaveTypeRequestDTO;
import com.gm.hrms.dto.response.LeaveTypeResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.entity.LeaveType;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.LeaveTypeMapper;
import com.gm.hrms.repository.LeaveTypeRepository;
import com.gm.hrms.service.LeaveTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LeaveTypeServiceImpl implements LeaveTypeService {

    private final LeaveTypeRepository repository;

    // ================= CREATE =================
    @Override
    public LeaveTypeResponseDTO create(LeaveTypeRequestDTO dto) {

        validateCode(dto.getCode());

        if (repository.existsByCode(dto.getCode().toUpperCase())) {
            throw new DuplicateResourceException("Leave code already exists");
        }

        // 🔥 NEW: CompOff validation (ONLY ADDITION)
        if (Boolean.TRUE.equals(dto.getIsCompOff())) {
            boolean exists = repository.existsByIsCompOffTrueAndIsActiveTrue();
            if (exists) {
                throw new InvalidRequestException("CompOff leave type already exists");
            }
        }

        LeaveType entity = LeaveTypeMapper.toEntity(dto);

        return LeaveTypeMapper.toResponse(repository.save(entity));
    }

    // ================= GET BY ID =================
    @Override
    public LeaveTypeResponseDTO getById(Long id) {

        LeaveType entity = repository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found"));

        return LeaveTypeMapper.toResponse(entity);
    }

    // ================= GET ALL =================
    @Override
    public PageResponseDTO<LeaveTypeResponseDTO> getAll(String search, Pageable pageable) {

        Page<LeaveType> page = repository.findAll(pageable);

        return PageResponseDTO.<LeaveTypeResponseDTO>builder()
                .content(page.getContent()
                        .stream()
                        .map(LeaveTypeMapper::toResponse)
                        .toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    // ================= PATCH UPDATE =================
    @Override
    public LeaveTypeResponseDTO update(Long id, LeaveTypeRequestDTO dto) {

        LeaveType entity = repository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found"));

        if (entity.getIsSystemDefined()) {
            throw new InvalidRequestException("System defined leave cannot be modified");
        }

        // 🔥 NEW: CompOff validation (ONLY ADDITION)
        if (dto.getIsCompOff() != null && dto.getIsCompOff()) {

            boolean exists = repository.existsByIsCompOffTrueAndIsActiveTrue();

            if (exists && !Boolean.TRUE.equals(entity.getIsCompOff())) {
                throw new InvalidRequestException("CompOff leave type already exists");
            }
        }

        //  CODE VALIDATION ONLY IF PROVIDED
        if (dto.getCode() != null) {

            validateCode(dto.getCode());

            if (!entity.getCode().equalsIgnoreCase(dto.getCode()) &&
                    repository.existsByCode(dto.getCode().toUpperCase())) {

                throw new DuplicateResourceException("Leave code already exists");
            }
        }

        //  APPLY PATCH VIA MAPPER
        LeaveTypeMapper.updateEntity(entity, dto);

        return LeaveTypeMapper.toResponse(repository.save(entity));
    }

    // ================= DELETE =================
    @Override
    public void delete(Long id) {

        LeaveType entity = repository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found"));

        if (entity.getIsSystemDefined()) {
            throw new InvalidRequestException("System defined leave cannot be deleted");
        }

        entity.setIsActive(false);
        entity.setDeletedAt(LocalDateTime.now());

        repository.save(entity);
    }

    // ================= VALIDATION =================
    private void validateCode(String code) {

        if (!code.matches("^[A-Z_]+$")) {
            throw new InvalidRequestException("Code must be uppercase (A-Z, _)");
        }
    }
}