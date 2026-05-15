package com.gm.hrms.dto.response;


import com.gm.hrms.enums.WorkMode;
import com.gm.hrms.enums.WorkingType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TraineeWorkDetailsResponseDTO {

    private String branchName;
    private Integer trainingPeriodMonths;

    private LocalDate trainingStartDate;
    private LocalDate trainingEndDate;

    private String trainingShiftTime;

    private WorkMode workMode;
    private WorkingType workingType;
}