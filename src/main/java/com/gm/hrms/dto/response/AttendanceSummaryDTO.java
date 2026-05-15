package com.gm.hrms.dto.response;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSummaryDTO {
    private LocalDate date;
    private int total;
    private int present;
    private int absent;
    private int halfDay;
    private int onLeave;
    private int lateArrivals;
}