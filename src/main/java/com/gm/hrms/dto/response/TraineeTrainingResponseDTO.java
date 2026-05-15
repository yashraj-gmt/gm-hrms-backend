package com.gm.hrms.dto.response;

import com.gm.hrms.enums.WorkMode;
import com.gm.hrms.enums.WorkingType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TraineeTrainingResponseDTO {

    private LocalDate startDate;
    private LocalDate endDate;
    private Integer trainingPeriodMonths;
    private Double stipend;
    private String shiftTiming;
    private WorkMode workMode;
    private WorkingType workingType;
    private String branchName;
}