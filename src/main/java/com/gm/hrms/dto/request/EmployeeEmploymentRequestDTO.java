package com.gm.hrms.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class EmployeeEmploymentRequestDTO {

    private LocalDate dateOfJoining;

    private Integer yearOfExperience;

    private Double ctc;

    private List<String> previousCompanyNames;

    private Integer noticePeriod;
}