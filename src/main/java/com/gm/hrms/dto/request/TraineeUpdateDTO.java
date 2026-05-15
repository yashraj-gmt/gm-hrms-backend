package com.gm.hrms.dto.request;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class TraineeUpdateDTO {

    private String traineeCode;

    @Valid
    private PersonalInformationRequestDTO personalInformation;

    @Valid
    private TraineeTrainingRequestDTO trainingDetails;

    @Valid
    private TraineeEducationRequestDTO educationDetails;

    @Valid
    private TraineeMentorRequestDTO mentorDetails;
}