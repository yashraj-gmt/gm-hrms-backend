package com.gm.hrms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RoleTransferRequestDTO {

    /** PersonalInformation ID of the recipient */
    @NotNull(message = "recipientPersonId is required")
    private Long recipientPersonId;

    private LocalDate startDate;
    private LocalDate endDate;

    @NotNull
    private Boolean isPermanent;

    @NotBlank(message = "reason is required")
    private String reason;

    /** The OTP entered by the user — must already be valid in DB before confirm */
    @NotBlank
    @jakarta.validation.constraints.Size(min = 6, max = 6)
    private String otp;
}