package com.gm.hrms.entity;

import com.gm.hrms.enums.CompOffStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "comp_off_requests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompOffRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  USER
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_id", nullable = false)
    private PersonalInformation personal;

    //  WORK DATE (work that day)
    @Column(nullable = false, name = "worked_date")
    private LocalDate workedDate;

    // how many days of compoff
    @Column(nullable = false, name = "earned_days")
    private Double earnedDays;

    private String reason;

    // ================= APPROVAL =================
    @Enumerated(EnumType.STRING)
    private CompOffStatus status;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    // ================= SYSTEM =================
    @Column(name = "is_active")
    private Boolean isActive;
}