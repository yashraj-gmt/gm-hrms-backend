package com.gm.hrms.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class TraineeResponseDTO extends BaseUserResponseDTO {

    private Long traineeId;

    private String traineeCode;

    private TraineeTrainingResponseDTO trainingDetails;

    private TraineeEducationResponseDTO educationDetails;

    private TraineeMentorResponseDTO mentorDetails;
}