package com.gm.hrms.dto.request;

import lombok.Data;

@Data
public class TraineeEducationRequestDTO {

    // 12th / HSC
    private String hscCompletion;
    private Integer hscYear;

    // Bachelor
    private String bachelorCompletion;
    private Integer bachelorYear;

    // Master (optional)
    private String masterCompletion;
    private Integer masterYear;

    // Degree
    private String degreeName;
    private String degreeResult;

    // University
    private String universityName;
    private String universityAddress;

    // Training completion
    private String trainingCompletionStatus;   // Complete | Pending | On Going
}