package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.LeaveTypeRequestDTO;
import com.gm.hrms.dto.response.LeaveTypeResponseDTO;
import com.gm.hrms.entity.LeaveType;

public class LeaveTypeMapper {

    private LeaveTypeMapper() {}

    // ================= CREATE =================
    public static LeaveType toEntity(LeaveTypeRequestDTO dto) {
        return LeaveType.builder()
                .name(dto.getName())
                .code(dto.getCode().toUpperCase())
                .description(dto.getDescription())
                .isPaid(dto.getIsPaid())
                .allowDuringProbation(dto.getAllowDuringProbation())
                .allowHalfDay(dto.getAllowHalfDay())
                .isCompOff(dto.getIsCompOff())
                .isActive(true)
                .isSystemDefined(false)
                .build();
    }

    // ================= RESPONSE =================
    public static LeaveTypeResponseDTO toResponse(LeaveType entity) {
        return LeaveTypeResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .description(entity.getDescription())
                .isPaid(entity.getIsPaid())
                .allowHalfDay(entity.getAllowHalfDay())
                .allowDuringProbation(entity.getAllowDuringProbation())
                .isActive(entity.getIsActive())
                .isCompOff(entity.getIsCompOff())
                .isSystemDefined(entity.getIsSystemDefined())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // ================= PATCH UPDATE =================
    public static void updateEntity(LeaveType entity, LeaveTypeRequestDTO dto) {

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }

        if (dto.getCode() != null) {
            entity.setCode(dto.getCode().toUpperCase());
        }

        if (dto.getIsCompOff() != null) {
            entity.setIsCompOff(dto.getIsCompOff());
        }

        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getAllowDuringProbation() != null) {
            entity.setAllowDuringProbation(dto.getAllowDuringProbation());
        }

        if (dto.getIsPaid() != null) {
            entity.setIsPaid(dto.getIsPaid());
        }

        if (dto.getAllowHalfDay() != null) {
            entity.setAllowHalfDay(dto.getAllowHalfDay());
        }
    }
}