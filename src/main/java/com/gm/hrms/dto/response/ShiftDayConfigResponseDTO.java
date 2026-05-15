package com.gm.hrms.dto.response;

import lombok.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftDayConfigResponseDTO {
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isWeekOff;
    private LocalTime checkinStartWindow;
    private LocalTime checkinEndWindow;
    private LocalTime checkoutStartWindow;
    private LocalTime checkoutEndWindow;
}