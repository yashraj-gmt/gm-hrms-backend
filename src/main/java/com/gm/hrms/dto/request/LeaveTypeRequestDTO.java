package com.gm.hrms.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class LeaveTypeRequestDTO {

    @NotBlank(message = "Leave name is required")
    private String name;

    @NotBlank(message = "Leave code is required")
    @Size(max = 10, message = "Code max length is 10")
    private String code;

    private String description;

    @NotNull(message = "Paid flag is required")
    private Boolean isPaid;

    @NotNull(message = "CompOff flag is required")
    private Boolean isCompOff;

    @NotNull(message = "Probation rule is required")
    private Boolean allowDuringProbation;

    @NotNull(message = "Half day flag is required")
    private Boolean allowHalfDay;
}