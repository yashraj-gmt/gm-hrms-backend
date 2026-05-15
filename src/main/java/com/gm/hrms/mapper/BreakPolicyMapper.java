package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.BreakPolicyRequestDTO;
import com.gm.hrms.dto.response.BreakPolicyResponseDTO;
import com.gm.hrms.entity.BreakPolicy;

public class BreakPolicyMapper {

    private BreakPolicyMapper() {}

    public static BreakPolicy toEntity(BreakPolicyRequestDTO dto) {
        return BreakPolicy.builder()
                .breakName(dto.getBreakName())
                .breakCategory(dto.getBreakCategory())
                .breakStart(dto.getBreakStart())
                .breakEnd(dto.getBreakEnd())
                .breakDurationMinutes(dto.getBreakDurationMinutes())
                .isPaid(dto.getIsPaid())
                .build();
    }

    public static void patchEntity(BreakPolicy entity, BreakPolicyRequestDTO dto) {

        if (dto.getBreakName()            != null) entity.setBreakName(dto.getBreakName());
        if (dto.getBreakCategory()        != null) entity.setBreakCategory(dto.getBreakCategory());
        if (dto.getBreakStart()           != null) entity.setBreakStart(dto.getBreakStart());
        if (dto.getBreakEnd()             != null) entity.setBreakEnd(dto.getBreakEnd());
        if (dto.getBreakDurationMinutes() != null) entity.setBreakDurationMinutes(dto.getBreakDurationMinutes());
        if (dto.getIsPaid()               != null) entity.setIsPaid(dto.getIsPaid());

        // ✅ Added — supports toggle active/inactive via PATCH
        if (dto.getIsActive()             != null) entity.setIsActive(dto.getIsActive());
    }

    public static BreakPolicyResponseDTO toResponse(BreakPolicy entity) {
        return BreakPolicyResponseDTO.builder()
                .id(entity.getId())
                .breakName(entity.getBreakName())
                .breakCategory(entity.getBreakCategory())
                .breakStart(entity.getBreakStart())
                .breakEnd(entity.getBreakEnd())
                .breakDurationMinutes(entity.getBreakDurationMinutes())
                .isPaid(entity.getIsPaid())
                // ✅ Added — exposed so the frontend can render status badge
                .isActive(entity.getIsActive())
                .build();
    }
}