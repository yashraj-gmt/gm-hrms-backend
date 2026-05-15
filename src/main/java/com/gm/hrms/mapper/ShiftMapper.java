package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.*;
import com.gm.hrms.dto.response.*;
import com.gm.hrms.entity.*;
import java.util.List;

public class ShiftMapper {

    private ShiftMapper() {}

    // ── ENTITY ────────────────────────────────────────────────────────────────
    public static Shift toEntity(ShiftRequestDTO dto) {
        return Shift.builder()
                .shiftName(dto.getShiftName())
                .shiftType(dto.getShiftType())
                .graceMinutes(dto.getGraceMinutes())
                .minimumWorkHours(dto.getMinimumWorkHours())
                .lateMarkAfterMinutes(dto.getLateMarkAfterMinutes())
                .lateMarkLimit(dto.getLateMarkLimit())
                .overtimeAllowed(dto.getOvertimeAllowed())
                .overtimeAfterMinutes(dto.getOvertimeAfterMinutes())
                .autoCheckout(dto.getAutoCheckout())
                .build();
    }

    // ── NORMAL TIMING ─────────────────────────────────────────────────────────
    public static ShiftTiming toTiming(ShiftTimingDTO dto, Shift shift) {
        if (dto == null) return null;
        return ShiftTiming.builder()
                .shift(shift)
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .checkinStartWindow(dto.getCheckinStartWindow())
                .checkinEndWindow(dto.getCheckinEndWindow())
                .checkoutStartWindow(dto.getCheckoutStartWindow())
                .checkoutEndWindow(dto.getCheckoutEndWindow())
                .saturdayOff(dto.getSaturdayOff())
                .sundayOff(dto.getSundayOff())
                .build();
    }

    // ── CUSTOM DAY CONFIGS ────────────────────────────────────────────────────
    public static List<ShiftDayConfig> toDayConfigs(List<ShiftDayConfigDTO> dtos, Shift shift) {
        if (dtos == null) return List.of();
        return dtos.stream()
                .map(d -> ShiftDayConfig.builder()
                        .shift(shift)
                        .dayOfWeek(d.getDayOfWeek())
                        .startTime(d.getStartTime())
                        .endTime(d.getEndTime())
                        .isWeekOff(d.getIsWeekOff())
                        .checkinStartWindow(d.getCheckinStartWindow())
                        .checkinEndWindow(d.getCheckinEndWindow())
                        .checkoutStartWindow(d.getCheckoutStartWindow())
                        .checkoutEndWindow(d.getCheckoutEndWindow())
                        .build())
                .toList();
    }

    // ── BREAK MAPPINGS ────────────────────────────────────────────────────────
    public static List<ShiftBreakMapping> toBreakMappings(List<Long> breakIds, Shift shift) {
        if (breakIds == null) return List.of();
        return breakIds.stream()
                .map(id -> ShiftBreakMapping.builder()
                        .shift(shift)
                        .breakPolicy(BreakPolicy.builder().id(id).build())
                        .build())
                .toList();
    }

    // ── RESPONSE ──────────────────────────────────────────────────────────────
    public static ShiftResponseDTO toResponse(Shift entity) {
        ShiftTimingResponseDTO timing = null;
        if (entity.getTiming() != null) {
            ShiftTiming t = entity.getTiming();
            timing = ShiftTimingResponseDTO.builder()
                    .startTime(t.getStartTime())
                    .endTime(t.getEndTime())
                    .checkinStartWindow(t.getCheckinStartWindow())
                    .checkinEndWindow(t.getCheckinEndWindow())
                    .checkoutStartWindow(t.getCheckoutStartWindow())
                    .checkoutEndWindow(t.getCheckoutEndWindow())
                    .saturdayOff(t.getSaturdayOff())
                    .sundayOff(t.getSundayOff())
                    .build();
        }

        List<ShiftDayConfigResponseDTO> dayConfigs =
                entity.getDayConfigs() == null ? List.of() :
                        entity.getDayConfigs().stream()
                                .map(d -> ShiftDayConfigResponseDTO.builder()
                                        .dayOfWeek(d.getDayOfWeek())
                                        .startTime(d.getStartTime())
                                        .endTime(d.getEndTime())
                                        .isWeekOff(d.getIsWeekOff())
                                        .checkinStartWindow(d.getCheckinStartWindow())
                                        .checkinEndWindow(d.getCheckinEndWindow())
                                        .checkoutStartWindow(d.getCheckoutStartWindow())
                                        .checkoutEndWindow(d.getCheckoutEndWindow())
                                        .build())
                                .toList();

        List<BreakPolicyResponseDTO> breaks =
                entity.getBreakMappings() == null ? List.of() :
                        entity.getBreakMappings().stream()
                                .map(m -> {
                                    BreakPolicy b = m.getBreakPolicy();
                                    return BreakPolicyResponseDTO.builder()
                                            .id(b.getId())
                                            .breakName(b.getBreakName())
                                            .breakCategory(b.getBreakCategory())
                                            .breakStart(b.getBreakStart())
                                            .breakEnd(b.getBreakEnd())
                                            .breakDurationMinutes(b.getBreakDurationMinutes())
                                            .isPaid(b.getIsPaid())
                                            .build();
                                })
                                .toList();

        return ShiftResponseDTO.builder()
                .id(entity.getId())
                .shiftName(entity.getShiftName())
                .shiftType(entity.getShiftType().name())
                .graceMinutes(entity.getGraceMinutes())
                .minimumWorkHours(entity.getMinimumWorkHours())
                .lateMarkAfterMinutes(entity.getLateMarkAfterMinutes())
                .lateMarkLimit(entity.getLateMarkLimit())
                .overtimeAllowed(entity.getOvertimeAllowed())
                .overtimeAfterMinutes(entity.getOvertimeAfterMinutes())
                .autoCheckout(entity.getAutoCheckout())
                .isActive(entity.getIsActive())
                .normalTiming(timing)
                .dayConfigs(dayConfigs)
                .breaks(breaks)
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .build();
    }
}