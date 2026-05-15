package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.InternCourseRequestDTO;
import com.gm.hrms.dto.response.InternCourseResponseDTO;
import com.gm.hrms.entity.InternCourse;

public class InternCourseMapper {

    private InternCourseMapper() {}

    /**
     * RequestDTO → new Entity (for CREATE).
     * status defaults to true via @Builder.Default on entity
     * if dto.getStatus() is null.
     */
    public static InternCourse toEntity(InternCourseRequestDTO dto) {
        return InternCourse.builder()
                .name(dto.getName().trim())
                .description(dto.getDescription() != null ? dto.getDescription().trim() : null)
                .status(dto.getStatus() != null ? dto.getStatus() : true)
                .build();
    }

    /**
     * Applies RequestDTO fields onto existing Entity (for PATCH/UPDATE).
     * Only updates status when explicitly provided (non-null) in the DTO.
     */
    public static void patchUpdate(InternCourse entity, InternCourseRequestDTO dto) {
        if (dto.getName() != null) {
            entity.setName(dto.getName().trim());
        }
        // Allow clearing description by sending empty string
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription().trim());
        }
        // Only toggle status when explicitly sent
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
    }

    /**
     * Entity → ResponseDTO.
     * Maps entity.status → responseDTO.active for frontend field naming.
     */
    public static InternCourseResponseDTO toResponse(InternCourse entity) {
        return InternCourseResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .active(entity.getStatus())   // status → active
                .build();
    }
}