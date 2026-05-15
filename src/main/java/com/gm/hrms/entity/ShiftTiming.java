package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
@Entity
@Table(name = "shift_timings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftTiming {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "shift_id")
    private Shift shift;

    private LocalTime startTime;

    private LocalTime endTime;

    private LocalTime checkinStartWindow;

    private LocalTime checkinEndWindow;

    private LocalTime checkoutStartWindow;

    private LocalTime checkoutEndWindow;

    private Boolean saturdayOff;

    private Boolean sundayOff;
}