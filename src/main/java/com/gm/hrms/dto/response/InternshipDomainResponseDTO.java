package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InternshipDomainResponseDTO {

    private Long id;
    private String name;
    private String description;
    private Boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}