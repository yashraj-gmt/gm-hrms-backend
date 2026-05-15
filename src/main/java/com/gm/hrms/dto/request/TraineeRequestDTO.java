package com.gm.hrms.dto.request;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class TraineeRequestDTO {

    @Valid
    private TraineeTrainingRequestDTO trainingDetails;

    @Valid
    private TraineeEducationRequestDTO educationDetails;

    @Valid
    private TraineeMentorRequestDTO mentorDetails;
}