package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DepartmentResponseDTO {

    private Long                        id;
    private String                      name;
    private String                      code;
    private String                      description;
    private Boolean                     status;
    private List<SubDepartmentResponseDTO> subDepartments;
}