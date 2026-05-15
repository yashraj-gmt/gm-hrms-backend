package com.gm.hrms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LeaveCancelRequestDTO {

    @NotBlank(message = "Cancel reason is required")
    @Size(min = 5, max = 300, message = "Reason must be 5–300 characters")
    private String cancelReason;
}