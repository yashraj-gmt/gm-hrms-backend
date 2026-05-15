package com.gm.hrms.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TraineeTrainingRequestDTO {

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer trainingPeriodMonths;

    private Double stipend;

    private String shiftId;          // shift reference (optional, stored as WorkProfile shift)

    private String workingType;      // FULL_TIME | PART_TIME | CONTRACTUAL

    private String workMode;         // REMOTE | HYBRID | ON_SITE
}