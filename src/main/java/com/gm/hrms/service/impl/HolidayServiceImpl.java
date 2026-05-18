package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.HolidayRequestDTO;
import com.gm.hrms.dto.response.HolidayResponseDTO;
import com.gm.hrms.dto.response.HolidayStatsDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.entity.Holiday;
import com.gm.hrms.enums.HolidayType;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.HolidayMapper;
import com.gm.hrms.repository.HolidayRepository;
import com.gm.hrms.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayServiceImpl implements HolidayService {

    private final HolidayRepository repository;

    @Override
    @Transactional
    public HolidayResponseDTO create(HolidayRequestDTO dto) {

        if (repository.existsByHolidayNameAndHolidayDateAndIsDeletedFalse(
                dto.getHolidayName(), dto.getHolidayDate())) {
            throw new DuplicateResourceException("Holiday already exists for this date");
        }

        Holiday entity = HolidayMapper.toEntity(dto);
        entity.setIsActive(true);
        entity.setIsDeleted(false);
        entity.setCreatedAt(LocalDateTime.now());

        return HolidayMapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public HolidayResponseDTO update(Long id, HolidayRequestDTO dto) {

        Holiday entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Holiday not found with id: " + id));

        HolidayMapper.patchEntity(entity, dto);

        return HolidayMapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public HolidayResponseDTO getById(Long id) {

        Holiday entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Holiday not found with id: " + id));

        return HolidayMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<HolidayResponseDTO> getAll(
            String search, HolidayType type, Boolean isActive, Boolean isOptional, Pageable pageable) {

        String searchParam = (search == null || search.isBlank()) ? null : search.trim();

        Page<Holiday> page = repository.search(searchParam, type, isActive, isOptional, pageable);

        List<HolidayResponseDTO> content = page.getContent()
                .stream()
                .map(HolidayMapper::toResponse)
                .toList();

        return PageResponseDTO.<HolidayResponseDTO>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public HolidayStatsDTO getStats() {
        LocalDate today = LocalDate.now();
        return HolidayStatsDTO.builder()
                .total(    repository.countByIsDeletedFalse())
                .active(   repository.countByIsDeletedFalseAndIsActiveTrue())
                .upcoming( repository.countByIsDeletedFalseAndHolidayDateBetween(today, today.plusDays(90)))
                .optional( repository.countByIsDeletedFalseAndIsOptionalTrue())
                .build();
    }

    @Override
    @Transactional
    public void delete(Long id) {

        Holiday entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Holiday not found with id: " + id));

        entity.setIsDeleted(true);   // soft-delete; isActive left as-is
        repository.save(entity);
    }
}