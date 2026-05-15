package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.DesignationRequestDTO;
import com.gm.hrms.dto.response.DesignationResponseDTO;
import com.gm.hrms.entity.Designation;

public class DesignationMapper {

    private DesignationMapper(){}

    public static Designation toEntity(DesignationRequestDTO dto) {

        return Designation.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
    }

    public static void updateEntity(Designation entity, DesignationRequestDTO dto) {

        if(dto.getName() != null)
            entity.setName(dto.getName());

        if(dto.getDescription() != null)
            entity.setDescription(dto.getDescription());

        if(dto.getActive() != null)
            entity.setActive(dto.getActive());
    }

    public static DesignationResponseDTO toResponse(Designation entity) {

        return DesignationResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .active(entity.getActive())
                .build();
    }
}