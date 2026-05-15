package com.gm.hrms.dto.request;

import com.gm.hrms.enums.ShiftType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftRequestDTO {

    @NotBlank(message = "Shift name is required")
    private String shiftName;

    @NotNull(message = "Shift type is required")
    private ShiftType shiftType;

    private Integer graceMinutes;
    private Integer lateMarkAfterMinutes;
    private Integer lateMarkLimit;
    private Integer minimumWorkHours;
    private Boolean overtimeAllowed;
    private Integer overtimeAfterMinutes;
    private Boolean autoCheckout;

    private ShiftTimingDTO normalTiming;

    private List<ShiftDayConfigDTO> dayConfigs;

    private List<Long> breakIds;
}