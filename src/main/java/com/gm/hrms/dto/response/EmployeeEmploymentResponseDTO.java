package com.gm.hrms.dto.response;

import com.gm.hrms.enums.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class EmployeeEmploymentResponseDTO {

    private LocalDate dateOfJoining;
    private Integer yearOfExperience;
    private Double ctc;

    private List<String> previousCompanyNames;

    private Integer noticePeriod;

}