package com.gm.hrms.dto.request;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class InternRequestDTO {

    @Valid
    private InternCollegeRequestDTO collegeDetails;

    @Valid
    private InternInternshipRequestDTO internshipDetails;

    @Valid
    private InternMentorRequestDTO mentorDetails;
}