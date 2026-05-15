package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;



@Entity
@Table(name = "shift_day_configs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftDayConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shift_id")
    private Shift shift;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    private LocalTime startTime;

    private LocalTime endTime;

    private Boolean isWeekOff;

    private LocalTime checkinStartWindow;

    private LocalTime checkinEndWindow;

    private LocalTime checkoutStartWindow;

    private LocalTime checkoutEndWindow;
}