package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InternCollegeResponseDTO {

    private String courseName;
    private String semester;
    private String academicYear;

    private String enrollmentNumber;
    private String collegeName;
    private String collegeAddress;

    private String universityName;
    private String degreeCompletionStatus;
    private Integer year;
}