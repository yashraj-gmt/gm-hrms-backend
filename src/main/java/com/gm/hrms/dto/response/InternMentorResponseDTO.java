package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InternMentorResponseDTO {

    private Long mentorEmployeeId;
    private String mentorName;
    private String mentorDesignation;

    private Long supervisorEmployeeId;
    private String supervisorName;
    private String supervisorDesignation;
}