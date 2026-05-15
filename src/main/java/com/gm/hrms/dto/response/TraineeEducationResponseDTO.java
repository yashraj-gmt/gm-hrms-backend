package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TraineeEducationResponseDTO {

    private String hscCompletion;
    private Integer hscYear;

    private String bachelorCompletion;
    private Integer bachelorYear;

    private String masterCompletion;
    private Integer masterYear;

    private String degreeName;
    private String degreeResult;

    private String universityName;
    private String universityAddress;

    private String trainingCompletionStatus;
}