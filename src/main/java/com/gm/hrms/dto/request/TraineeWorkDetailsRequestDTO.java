package com.gm.hrms.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TraineeWorkDetailsRequestDTO {

    @Min(value = 1, message = "Training period must be at least 1 month")
    private Integer trainingPeriodMonths;

    private LocalDate trainingStartDate;

    private LocalDate trainingEndDate;
}