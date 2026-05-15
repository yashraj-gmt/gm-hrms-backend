package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.DocumentTypeRequestDTO;
import com.gm.hrms.dto.response.DocumentTypeResponseDTO;
import com.gm.hrms.entity.DocumentType;

public class DocumentTypeMapper {

    private DocumentTypeMapper() {}

    /**
     * RequestDTO → new Entity (for create)
     * Sets active = true by default on creation.
     */
    public static DocumentType toEntity(DocumentTypeRequestDTO dto) {
        return DocumentType.builder()
                .name(dto.getName().trim())
                .docKey(dto.getKey().trim())
                .applicableTypes(dto.getApplicableTypes())
                .mandatory(dto.getMandatory() != null ? dto.getMandatory() : false)
                .active(true)   // always active on creation
                .build();
    }

    /**
     * Applies RequestDTO fields onto an existing Entity (for update/PATCH).
     * active is updated only when explicitly provided in the DTO.
     */
    public static void updateEntity(DocumentType entity, DocumentTypeRequestDTO dto) {
        entity.setName(dto.getName().trim());
        entity.setDocKey(dto.getKey().trim());
        entity.setApplicableTypes(dto.getApplicableTypes());
        entity.setMandatory(dto.getMandatory() != null ? dto.getMandatory() : entity.getMandatory());

        // active is optional in PATCH — only update when explicitly provided
        if (dto.getActive() != null) {
            entity.setActive(dto.getActive());
        }
    }

    /**
     * Entity → ResponseDTO
     */
    public static DocumentTypeResponseDTO toResponse(DocumentType entity) {
        return DocumentTypeResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .key(entity.getDocKey())
                .applicableTypes(entity.getApplicableTypes())
                .mandatory(entity.getMandatory())
                .active(entity.getActive())
                .build();
    }
}