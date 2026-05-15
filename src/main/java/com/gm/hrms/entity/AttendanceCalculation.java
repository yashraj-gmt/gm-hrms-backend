package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_calculation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceCalculation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "attendance_id")
    private Attendance attendance;

    @Column(name = "work_minutes")
    private Integer workMinutes;

    @Column(name = "break_minutes")
    private Integer breakMinutes;

    @Column(name = "late_minutes")
    private Integer lateMinutes;

    @Column(name = "overtime_minutes")
    private Integer overtimeMinutes;
}