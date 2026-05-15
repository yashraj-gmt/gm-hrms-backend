package com.gm.hrms.entity;

import com.gm.hrms.enums.RoleType;
import com.gm.hrms.enums.TransferStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "role_transfer_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleTransferRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The role being transferred FROM (always HR in this design) */
    @Enumerated(EnumType.STRING)
    @Column(name = "source_role", nullable = false, length = 20)
    private RoleType sourceRole;

    /** The user receiving the delegated role */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_personal_info_id", nullable = false)
    private PersonalInformation recipient;

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

    /** Username of HR/Admin who initiated the request */
    @Column(name = "initiated_by", nullable = false)
    private String initiatedBy;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancelled_by")
    private String cancelledBy;
}