package com.gm.hrms.dto.request;

import com.gm.hrms.enums.*;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PersonalInformationRequestDTO {

    private String firstName;
    private String middleName;
    private String lastName;
    private Gender gender;
    private LocalDate dateOfBirth;
    private EmploymentType employmentType;
    private MaritalStatus maritalStatus;
    private String spouseOrParentName;

    private String profileImageUrl;

    // Contact
    private String personalPhone;
    private String emergencyPhone;

    @Email(message = "Invalid email format")
    private String personalEmail;

    @Email(message = "Invalid email format")
    private String officeEmail;

    private PersonalAddressRequestDTO address;
    private BankDetailsRequestDTO bankDetails;
    private WorkProfileRequestDTO workProfile;

    private RecordStatus status;
}