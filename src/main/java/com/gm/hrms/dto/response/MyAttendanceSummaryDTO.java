package com.gm.hrms.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyAttendanceSummaryDTO {
    private int total;
    private int present;
    private int absent;
    private int halfDay;
    private int onLeave;
    private int lateCount;
    private int totalWork;
    private int totalOT;

    // Overtime for the current month
    private String currentMonthName; // e.g., "June"
    private int currentMonthOT; // Overtime in minutes
}
