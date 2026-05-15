package com.gm.hrms.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentShiftResponseDTO {
    private ShiftAssignmentResponseDTO currentAssignment;
    private UpcomingShiftChangeDTO upcomingChange; // null if no upcoming change
}