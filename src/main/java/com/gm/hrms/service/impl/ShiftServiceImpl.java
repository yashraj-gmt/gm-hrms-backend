package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.*;
import com.gm.hrms.dto.response.*;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.ShiftType;
import com.gm.hrms.exception.*;
import com.gm.hrms.mapper.ShiftMapper;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.ShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository shiftRepository;
    private final ShiftTimingRepository shiftTimingRepository;
    private final ShiftDayConfigRepository shiftDayConfigRepository;
    private final ShiftBreakMappingRepository shiftBreakMappingRepository;

    // ── CREATE ────────────────────────────────────────────────────────────────
    @Override
    public ShiftResponseDTO create(ShiftRequestDTO dto) {
        if (dto.getShiftType() == ShiftType.NORMAL && dto.getNormalTiming() == null) {
            throw new InvalidRequestException("Normal shift requires normalTiming");
        }
        if (dto.getShiftType() == ShiftType.CUSTOM &&
                (dto.getDayConfigs() == null || dto.getDayConfigs().isEmpty())) {
            throw new InvalidRequestException("Custom shift requires dayConfigs (7 days)");
        }

        Shift shift = ShiftMapper.toEntity(dto);
        shift.setIsActive(true);
        shift.setCreatedAt(LocalDateTime.now());
        shift.setCreatedBy(getCurrentUsername());
        shiftRepository.save(shift);

        if (dto.getShiftType() == ShiftType.NORMAL) {
            shiftTimingRepository.save(ShiftMapper.toTiming(dto.getNormalTiming(), shift));
        }
        if (dto.getShiftType() == ShiftType.CUSTOM) {
            shiftDayConfigRepository.saveAll(ShiftMapper.toDayConfigs(dto.getDayConfigs(), shift));
        }
        if (dto.getBreakIds() != null && !dto.getBreakIds().isEmpty()) {
            shiftBreakMappingRepository.saveAll(ShiftMapper.toBreakMappings(dto.getBreakIds(), shift));
        }

        return ShiftMapper.toResponse(
                shiftRepository.findById(shift.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Shift not found")));
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    @Override
    public ShiftResponseDTO update(Long id, ShiftRequestDTO dto) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found"));

        // Patch scalar fields
        if (dto.getShiftName() != null)          shift.setShiftName(dto.getShiftName());
        if (dto.getGraceMinutes() != null)        shift.setGraceMinutes(dto.getGraceMinutes());
        if (dto.getLateMarkAfterMinutes() != null) shift.setLateMarkAfterMinutes(dto.getLateMarkAfterMinutes());
        if (dto.getLateMarkLimit() != null)        shift.setLateMarkLimit(dto.getLateMarkLimit());
        if (dto.getMinimumWorkHours() != null)     shift.setMinimumWorkHours(dto.getMinimumWorkHours());
        if (dto.getOvertimeAllowed() != null)      shift.setOvertimeAllowed(dto.getOvertimeAllowed());
        if (dto.getOvertimeAfterMinutes() != null) shift.setOvertimeAfterMinutes(dto.getOvertimeAfterMinutes());
        if (dto.getAutoCheckout() != null)         shift.setAutoCheckout(dto.getAutoCheckout());
        shift.setUpdatedAt(LocalDateTime.now());
        shift.setUpdatedBy(getCurrentUsername());

        // Update normal timing
        if (shift.getShiftType() == ShiftType.NORMAL && dto.getNormalTiming() != null) {
            ShiftTiming timing = shift.getTiming();
            if (timing == null) {
                timing = ShiftMapper.toTiming(dto.getNormalTiming(), shift);
            } else {
                ShiftTimingDTO t = dto.getNormalTiming();
                if (t.getStartTime() != null)            timing.setStartTime(t.getStartTime());
                if (t.getEndTime() != null)              timing.setEndTime(t.getEndTime());
                if (t.getCheckinStartWindow() != null)   timing.setCheckinStartWindow(t.getCheckinStartWindow());
                if (t.getCheckinEndWindow() != null)     timing.setCheckinEndWindow(t.getCheckinEndWindow());
                if (t.getCheckoutStartWindow() != null)  timing.setCheckoutStartWindow(t.getCheckoutStartWindow());
                if (t.getCheckoutEndWindow() != null)    timing.setCheckoutEndWindow(t.getCheckoutEndWindow());
                if (t.getSaturdayOff() != null)          timing.setSaturdayOff(t.getSaturdayOff());
                if (t.getSundayOff() != null)            timing.setSundayOff(t.getSundayOff());
            }
            shiftTimingRepository.save(timing);
        }

        // Replace day configs for custom shift
        if (shift.getShiftType() == ShiftType.CUSTOM && dto.getDayConfigs() != null) {
            shiftDayConfigRepository.deleteAll(shift.getDayConfigs());
            shiftDayConfigRepository.saveAll(ShiftMapper.toDayConfigs(dto.getDayConfigs(), shift));
        }

        // Replace break mappings if provided
        if (dto.getBreakIds() != null) {
            shiftBreakMappingRepository.deleteAllByShiftId(shift.getId());
            if (!dto.getBreakIds().isEmpty()) {
                shiftBreakMappingRepository.saveAll(ShiftMapper.toBreakMappings(dto.getBreakIds(), shift));
            }
        }

        shiftRepository.save(shift);
        return ShiftMapper.toResponse(
                shiftRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Shift not found")));
    }

    // ── GET BY ID ─────────────────────────────────────────────────────────────
    @Override
    public ShiftResponseDTO getById(Long id) {
        return ShiftMapper.toResponse(
                shiftRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Shift not found")));
    }

    // ── GET ALL ───────────────────────────────────────────────────────────────
    @Override
    public PageResponseDTO<ShiftResponseDTO> getAll(Pageable pageable) {
        Page<Shift> page = shiftRepository.findAll(pageable);
        List<ShiftResponseDTO> content = page.getContent().stream()
                .map(ShiftMapper::toResponse).toList();
        return PageResponseDTO.<ShiftResponseDTO>builder()
                .content(content).page(page.getNumber()).size(page.getSize())
                .totalElements(page.getTotalElements()).totalPages(page.getTotalPages())
                .first(page.isFirst()).last(page.isLast()).build();
    }

    // ── TOGGLE STATUS ─────────────────────────────────────────────────────────
    @Override
    public ShiftResponseDTO toggleStatus(Long id) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found"));
        shift.setIsActive(!shift.getIsActive());
        shift.setUpdatedAt(LocalDateTime.now());
        shift.setUpdatedBy(getCurrentUsername());
        shiftRepository.save(shift);
        return ShiftMapper.toResponse(shift);
    }

    // ── SOFT DELETE ───────────────────────────────────────────────────────────
    @Override
    public void delete(Long id) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found"));
        shift.setIsActive(false);
        shift.setUpdatedAt(LocalDateTime.now());
        shift.setUpdatedBy(getCurrentUsername());
        shiftRepository.save(shift);
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────
    private String getCurrentUsername() {
        try {
            return org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication().getName();
        } catch (Exception e) {
            return "system";
        }
    }
}