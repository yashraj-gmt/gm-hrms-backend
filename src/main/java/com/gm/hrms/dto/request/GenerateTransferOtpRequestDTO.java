package com.gm.hrms.dto.request;

import com.gm.hrms.enums.TransferOtpPurpose;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GenerateTransferOtpRequestDTO {

    @NotNull(message = "purpose is required")
    private TransferOtpPurpose purpose;
}