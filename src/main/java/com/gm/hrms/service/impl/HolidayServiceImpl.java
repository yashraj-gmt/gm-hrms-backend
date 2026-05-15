package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.HolidayRequestDTO;
import com.gm.hrms.dto.response.HolidayResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.entity.Holiday;
import com.gm.hrms.mapper.HolidayMapper;
import com.gm.hrms.repository.HolidayRepository;
import com.gm.hrms.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayServiceImpl implements HolidayService {

    private final HolidayRepository repository;

    @Override
    public HolidayResponseDTO create(HolidayRequestDTO dto) {

        if(repository.existsByHolidayNameAndHolidayDate(dto.getHolidayName(), dto.getHolidayDate())){
            throw new RuntimeException("Holiday already exists for this date");
        }

        Holiday entity = HolidayMapper.toEntity(dto);

        entity.setIsActive(true);
        entity.setCreatedAt(LocalDateTime.now());

        repository.save(entity);

        return HolidayMapper.toResponse(entity);
    }

    @Override
    public HolidayResponseDTO update(Long id, HolidayRequestDTO dto) {

        Holiday entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Holiday not found"));

        HolidayMapper.patchEntity(entity, dto);

        repository.save(entity);

        return HolidayMapper.toResponse(entity);
    }

    @Override
    public HolidayResponseDTO getById(Long id) {

        Holiday entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Holiday not found"));

        return HolidayMapper.toResponse(entity);
    }

    @Override
    public PageResponseDTO<HolidayResponseDTO> getAll(Pageable pageable) {

        Page<Holiday> page = repository.findAll(pageable);

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
    public void delete(Long id) {

        Holiday entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Holiday not found"));

        entity.setIsActive(false);

        repository.save(entity);
    }
}