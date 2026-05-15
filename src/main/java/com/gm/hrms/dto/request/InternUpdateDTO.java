package com.gm.hrms.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gm.hrms.enums.Status;
import jakarta.validation.Valid;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InternUpdateDTO {

    // ===== PERSONAL (Delegated) =====
    @Valid
    private PersonalInformationRequestDTO personalInformation;

    // ===== CORE =====
    private String internCode;

    // ===== COLLEGE =====
    @Valid
    private InternCollegeRequestDTO collegeDetails;

    // ===== INTERNSHIP =====
    @Valid
    private InternInternshipRequestDTO internshipDetails;

    // ===== MENTOR =====
    @Valid
    private InternMentorRequestDTO mentorDetails;
}