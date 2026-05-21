package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.HolidayRequestDTO;
import com.gm.hrms.dto.response.HolidayResponseDTO;
import com.gm.hrms.entity.Holiday;

public class HolidayMapper {

    private HolidayMapper() {}

    public static Holiday toEntity(HolidayRequestDTO dto) {
        Holiday h = new Holiday();
        h.setHolidayName(dto.getHolidayName());
        h.setHolidayDate(dto.getHolidayDate());
        h.setHolidayType(dto.getHolidayType());
        h.setDescription(dto.getDescription());
        h.setIsOptional(dto.getIsOptional());
        h.setIsDeleted(false);
        return h;
    }

    public static void patchEntity(Holiday entity, HolidayRequestDTO dto) {
        if (dto.getHolidayName() != null) entity.setHolidayName(dto.getHolidayName());
        if (dto.getHolidayDate() != null) entity.setHolidayDate(dto.getHolidayDate());
        if (dto.getHolidayType() != null) entity.setHolidayType(dto.getHolidayType());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getIsOptional()  != null) entity.setIsOptional(dto.getIsOptional());
        if (dto.getIsActive()    != null) entity.setIsActive(dto.getIsActive());   // ← status toggle
    }

    public static HolidayResponseDTO toResponse(Holiday entity) {
        return HolidayResponseDTO.builder()
                .id(entity.getId())
                .holidayName(entity.getHolidayName())
                .holidayDate(entity.getHolidayDate())
                .holidayType(entity.getHolidayType())
                .description(entity.getDescription())
                .isOptional(entity.getIsOptional())
                .isActive(entity.getIsActive())
                .build();
    }
}