package com.gm.hrms.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftAssignmentRequestDTO {

    @NotEmpty(message = "Select at least one employee")
    private List<Long> personalInformationIds;

    @NotNull(message = "Shift ID is required")
    private Long shiftId;

    @NotNull(message = "Effective date is required")
    private LocalDate effectiveFrom;

    private LocalDate effectiveTo; // optional end date

    private String note;
}