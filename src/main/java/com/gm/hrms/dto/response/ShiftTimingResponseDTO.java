package com.gm.hrms.dto.response;

import lombok.*;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftTimingResponseDTO {
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalTime checkinStartWindow;
    private LocalTime checkinEndWindow;
    private LocalTime checkoutStartWindow;
    private LocalTime checkoutEndWindow;
    private Boolean saturdayOff;
    private Boolean sundayOff;
}