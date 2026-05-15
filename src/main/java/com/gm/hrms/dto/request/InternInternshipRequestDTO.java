package com.gm.hrms.dto.request;

import com.gm.hrms.enums.InternShipType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class InternInternshipRequestDTO {

    private Long domainId;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer trainingPeriodMonths;

    private InternShipType internshipType;

    private Double stipend;
}