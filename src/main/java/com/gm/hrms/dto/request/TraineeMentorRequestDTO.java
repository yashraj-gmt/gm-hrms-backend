package com.gm.hrms.dto.request;

import lombok.Data;

@Data
public class TraineeMentorRequestDTO {

    private Long mentorEmployeeId;

    private Long supervisorEmployeeId;
}