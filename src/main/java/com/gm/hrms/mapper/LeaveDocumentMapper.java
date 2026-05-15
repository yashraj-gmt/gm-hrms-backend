package com.gm.hrms.mapper;

import com.gm.hrms.dto.response.LeaveDocumentResponseDTO;
import com.gm.hrms.entity.LeaveDocument;

public class LeaveDocumentMapper {

    public static LeaveDocumentResponseDTO toResponse(LeaveDocument entity) {
        return LeaveDocumentResponseDTO.builder()
                .id(entity.getId())
                .fileName(entity.getFileName())
                .filePath(entity.getFilePath())
                .uploadedBy(entity.getUploadedBy().getFirstName())
                .uploadedAt(entity.getUploadedAt())
                .build();
    }
}