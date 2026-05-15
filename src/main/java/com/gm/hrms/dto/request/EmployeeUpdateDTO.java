package com.gm.hrms.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeUpdateDTO {

    // ===== PERSONAL (Delegated) =====
    @Valid
    private PersonalInformationRequestDTO personalInformation;

    // ===== CORE =====
    private String employeeCode;

    // ===== MODULES =====
    private EmployeeEmploymentRequestDTO employment;
}