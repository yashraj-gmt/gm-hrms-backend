package com.gm.hrms.entity;

import com.gm.hrms.enums.BreakCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "break_policies")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BreakPolicy extends  BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "break_name")
    private String breakName;

    @Enumerated(EnumType.STRING)
    @Column(name = "break_category")
    private BreakCategory breakCategory;

    @Column(name = "break_start")
    private LocalTime breakStart;

    @Column(name = "break_end")
    private LocalTime breakEnd;

    @Column(name = "break_duration_minutes")
    private Integer breakDurationMinutes;

    @Column(name = "is_paid")
    private Boolean isPaid;

    @Column(name = "is_active")
    private Boolean isActive;

}
