package com.gm.hrms.dto.request;

import lombok.Data;

@Data
public class InternMentorRequestDTO {

    private Long mentorEmployeeId;

    private Long supervisorEmployeeId;
}