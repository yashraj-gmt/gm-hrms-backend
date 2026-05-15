package com.gm.hrms.dto.response;

import com.gm.hrms.enums.Gender;
import com.gm.hrms.enums.RoleType;
import com.gm.hrms.enums.Status;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class InternResponseDTO extends BaseUserResponseDTO {

    private Long internId;

    private String internCode;

    private InternCollegeResponseDTO collegeDetails;
    private InternInternshipResponseDTO internshipDetails;
    private InternMentorResponseDTO mentorDetails;
}