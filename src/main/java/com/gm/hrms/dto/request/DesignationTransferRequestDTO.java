package com.gm.hrms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DesignationTransferRequestDTO {

    /** PersonalInformation ID — whose designation is changing */
    @NotNull(message = "personId is required")
    private Long personId;

    /** The designation they're coming FROM (designation ID) */
    @NotNull(message = "fromDesignationId is required")
    private Long fromDesignationId;

    /** Free-text name of the new designation */
    @NotBlank(message = "toDesignationName is required")
    private String toDesignationName;

    private LocalDate startDate;
    private LocalDate endDate;

    @NotNull
    private Boolean isPermanent;

    @NotBlank(message = "reason is required")
    private String reason;

    @NotBlank
    @jakarta.validation.constraints.Size(min = 6, max = 6)
    private String otp;
}