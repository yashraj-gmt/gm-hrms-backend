package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubDepartmentResponseDTO {

    private Long    id;
    private String  name;
    private String  code;
    private String  description;
    private Boolean status;
}