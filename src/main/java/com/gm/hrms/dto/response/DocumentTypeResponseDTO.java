package com.gm.hrms.dto.response;

import com.gm.hrms.enums.ApplicableType;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class DocumentTypeResponseDTO {

    private Long id;
    private String name;
    private String key;
    private Set<ApplicableType> applicableTypes;
    private Boolean mandatory;
    private Boolean active;
}