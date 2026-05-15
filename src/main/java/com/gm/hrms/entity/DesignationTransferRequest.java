package com.gm.hrms.entity;

import com.gm.hrms.enums.TransferStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a request to transfer/promote an employee's designation,
 * with OTP-backed confirmation.
 */
@Entity
@Table(name = "designation_transfer_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DesignationTransferRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Old designation (FK) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_designation_id")
    private Designation fromDesignation;

    /** New designation name (can be a new designation not yet in DB) */
    @Column(name = "to_designation_name", nullable = false)
    private String toDesignationName;

    /** Target designation FK — set after confirm if toDesignation exists */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_designation_id")
    private Designation toDesignation;

    /** The person whose designation is changing */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_information_id")
    private PersonalInformation personalInformation;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_permanent", nullable = false)
    private Boolean isPermanent = false;

    @Column(length = 1000)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TransferStatus status = TransferStatus.DRAFT;

    @Column(name = "otp_verified", nullable = false)
    @Builder.Default
    private Boolean otpVerified = false;

    @Column(name = "initiated_by", nullable = false)
    private String initiatedBy;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancelled_by")
    private String cancelledBy;
}