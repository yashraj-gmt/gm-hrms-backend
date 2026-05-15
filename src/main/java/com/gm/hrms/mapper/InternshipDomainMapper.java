package com.gm.hrms.mapper;

import com.gm.hrms.dto.response.InternshipDomainResponseDTO;
import com.gm.hrms.entity.InternshipDomain;

public class InternshipDomainMapper {

    private InternshipDomainMapper() {}

    public static InternshipDomainResponseDTO toResponse(InternshipDomain d) {

        return InternshipDomainResponseDTO.builder()
                .id(d.getId())
                .name(d.getName())
                .description(d.getDescription())
                .active(d.getActive())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}