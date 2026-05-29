package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentResponseDTO {

    private Long id;

    private String documentTypeName;

    private String docKey;

    private String filePath;

    private String reason;

    private Boolean mandatory;
}