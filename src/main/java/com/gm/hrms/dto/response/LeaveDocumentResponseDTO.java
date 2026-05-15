package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LeaveDocumentResponseDTO {

    private Long id;
    private String fileName;
    private String filePath;
    private String uploadedBy;
    private LocalDateTime uploadedAt;
}