package com.gm.hrms.dto.request;

import com.gm.hrms.enums.DayType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class LeaveRequestDTO {


    @NotNull(message = "Personal ID is required")
    private Long personalId;

    @NotNull(message = "Leave type ID is required")
    private Long leaveTypeId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Start day type is required")
    private DayType startDayType;

    @NotNull(message = "End day type is required")
    private DayType endDayType;

    @NotBlank(message = "Reason is required")
    @Size(min = 10, max = 500, message = "Reason must be 10–500 characters")
    private String reason;

    // IDs of employees to be notified
    private List<Long> notifyPersonalIds;
}