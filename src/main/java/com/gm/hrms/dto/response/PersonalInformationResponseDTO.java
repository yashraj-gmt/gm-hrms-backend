package com.gm.hrms.dto.response;

import com.gm.hrms.enums.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class PersonalInformationResponseDTO {

    private Long id;

    private String firstName;
    private String middleName;
    private String lastName;

    private Gender gender;
    private LocalDate dateOfBirth;
    private EmploymentType employmentType;

    private RecordStatus status;

    private MaritalStatus maritalStatus;
    private String spouseOrParentName;

    private String profileImageUrl;

    private String personalPhone;
    private String emergencyPhone;
    private String personalEmail;

    private Boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}