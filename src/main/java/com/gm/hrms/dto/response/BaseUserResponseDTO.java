package com.gm.hrms.dto.response;

import com.gm.hrms.enums.*;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@SuperBuilder
public class BaseUserResponseDTO {

    // ===== IDENTIFIERS =====
    private Long personalInformationId;

    // ===== PERSONAL =====
    private String firstName;
    private String middleName;
    private String lastName;
    private Gender gender;
    private LocalDate dateOfBirth;
    private MaritalStatus maritalStatus;
    private String spouseOrParentName;
    private Boolean active;

    // ===== ORGANIZATION =====
    private String departmentName;
    private String designationName;
    private String reportingManagerName;
    private RoleType role;
    private Status status;
    private RecordStatus recordStatus;

    // ===== CONTACT =====
    private ContactResponseDTO contact;
    private EmployeeBankDetailsResponseDTO bankDetails;

    // ===== ADDRESS =====
    private AddressResponseDTO currentAddress;
    private AddressResponseDTO permanentAddress;

    // ===== AUDIT =====
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}