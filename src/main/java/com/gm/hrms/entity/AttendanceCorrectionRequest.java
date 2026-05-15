// src/main/java/com/gm/hrms/entity/AttendanceCorrectionRequest.java
package com.gm.hrms.entity;

import com.gm.hrms.enums.CorrectionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_correction_requests")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AttendanceCorrectionRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id", nullable = false)
    private Attendance attendance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_information_id", nullable = false)
    private PersonalInformation personalInformation;

    @Column(name = "original_check_in")
    private LocalDateTime originalCheckIn;

    @Column(name = "original_check_out")
    private LocalDateTime originalCheckOut;

    @Column(name = "requested_check_in")
    private LocalDateTime requestedCheckIn;

    @Column(name = "requested_check_out")
    private LocalDateTime requestedCheckOut;

    @Column(length = 600)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CorrectionStatus status = CorrectionStatus.PENDING;

    @Column(length = 600)
    private String remarks;

    @Column(name = "reviewed_by")
    private String reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
}