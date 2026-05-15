package com.gm.hrms.dto.request;

import com.gm.hrms.enums.TransferOtpPurpose;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VerifyTransferOtpRequestDTO {

    @NotNull
    private TransferOtpPurpose purpose;

    @NotBlank
    @Size(min = 6, max = 6, message = "OTP must be exactly 6 digits")
    private String otp;
}