package com.gm.hrms.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CompOffRequestDTO {

    @NotNull
    private Long personalId;

    @NotNull
    private LocalDate workedDate;

    @NotNull
    @DecimalMin("0.5")
    @DecimalMax("2.0")
    private Double earnedDays;

    private String reason;
}