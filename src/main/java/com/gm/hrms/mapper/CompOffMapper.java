package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.CompOffRequestDTO;
import com.gm.hrms.dto.response.CompOffResponseDTO;
import com.gm.hrms.entity.CompOffRequest;
import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.enums.CompOffStatus;

public class CompOffMapper {

    public static CompOffRequest toEntity(
            CompOffRequestDTO dto,
            PersonalInformation personal
    ) {
        return CompOffRequest.builder()
                .personal(personal)
                .workedDate(dto.getWorkedDate())
                .earnedDays(dto.getEarnedDays())
                .reason(dto.getReason())
                .status(CompOffStatus.PENDING)
                .isActive(true)
                .build();
    }

    public static CompOffResponseDTO toResponse(CompOffRequest entity) {

        return CompOffResponseDTO.builder()
                .id(entity.getId())
                .personalId(entity.getPersonal().getId())
                .workedDate(entity.getWorkedDate())
                .earnedDays(entity.getEarnedDays())
                .reason(entity.getReason())
                .status(entity.getStatus())
                .approvedBy(entity.getApprovedBy())
                .approvedAt(entity.getApprovedAt())
                .build();
    }
}