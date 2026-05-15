package com.gm.hrms.dto.request;

import lombok.Data;

@Data
public class InternCollegeRequestDTO {

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