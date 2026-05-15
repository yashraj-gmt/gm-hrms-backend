package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.PayrollComponentRequestDTO;
import com.gm.hrms.dto.response.PayrollComponentResponseDTO;
import com.gm.hrms.entity.PayrollComponent;

public class PayrollComponentMapper {

    public static PayrollComponent toEntity(PayrollComponentRequestDTO dto) {
        return PayrollComponent.builder()
                .name(dto.getName())
                .code(dto.getCode().toUpperCase())
                .type(dto.getType())
                .description(dto.getDescription())
                .displayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 99)
                .isSystemDefined(false)
                .isActive(true)
                .build();
    }

    public static void patchEntity(PayrollComponent entity, PayrollComponentRequestDTO dto) {
        if (dto.getName()         != null) entity.setName(dto.getName());
        if (dto.getDescription()  != null) entity.setDescription(dto.getDescription());
        if (dto.getDisplayOrder() != null) entity.setDisplayOrder(dto.getDisplayOrder());
        // code and type are intentionally NOT patchable after creation
    }

    public static PayrollComponentResponseDTO toResponse(PayrollComponent e) {
        return PayrollComponentResponseDTO.builder()
                .id(e.getId())
                .name(e.getName())
                .code(e.getCode())
                .type(e.getType())
                .description(e.getDescription())
                .displayOrder(e.getDisplayOrder())
                .isSystemDefined(e.getIsSystemDefined())
                .isActive(e.getIsActive())
                .build();
    }
}