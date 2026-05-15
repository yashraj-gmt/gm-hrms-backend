package com.gm.hrms.dto.response;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpcomingShiftChangeDTO {
    private String newShiftName;
    private LocalDate effectiveDate;
    private String assignedBy;
}