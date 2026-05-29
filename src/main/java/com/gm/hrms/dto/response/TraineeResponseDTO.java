package com.gm.hrms.dto.response;

import com.gm.hrms.enums.WorkMode;
import com.gm.hrms.enums.WorkingType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class TraineeResponseDTO extends BaseUserResponseDTO {

    private Long traineeId;

    private String traineeCode;

    private String branchName;

    private String shiftTiming;

    private WorkMode workMode;

    private WorkingType workingType;

    private TraineeTrainingResponseDTO trainingDetails;

    private TraineeEducationResponseDTO educationDetails;

    private TraineeMentorResponseDTO mentorDetails;
}