package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DesignationResponseDTO {

    private Long id;

    private String name;

    private String description;

    private Boolean active;
}