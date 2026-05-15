package com.gm.hrms.entity;

import com.gm.hrms.enums.AttendanceLogType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_information_id", nullable = false)
    private PersonalInformation personalInformation;

    @Column(name = "log_time")
    private LocalDateTime logTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "log_type")
    private AttendanceLogType logType;

    @Column(name = "device_type")
    private String deviceType;

}