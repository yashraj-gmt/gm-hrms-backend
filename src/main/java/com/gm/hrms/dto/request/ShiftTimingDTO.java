package com.gm.hrms.dto.request;

import lombok.*;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftTimingDTO {
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalTime checkinStartWindow;
    private LocalTime checkinEndWindow;
    private LocalTime checkoutStartWindow;
    private LocalTime checkoutEndWindow;
    private Boolean saturdayOff;
    private Boolean sundayOff;
}