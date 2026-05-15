package com.gm.hrms.dto.request;

import com.gm.hrms.enums.BreakCategory;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalTime;

@Data
public class BreakPolicyRequestDTO {

    @NotBlank(message = "Break name is required")
    private String        breakName;

    private BreakCategory breakCategory;   // FIXED | FLEXIBLE

    private LocalTime     breakStart;      // null for FLEXIBLE

    private LocalTime     breakEnd;        // null for FLEXIBLE

    private Integer       breakDurationMinutes;

    private Boolean       isPaid;

    private Boolean       isActive;
}