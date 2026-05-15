package com.gm.hrms.entity;

import com.gm.hrms.enums.TransferOtpPurpose;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "transfer_otp_tokens",
        indexes = {
                @Index(name = "idx_tot_username", columnList = "username"),
                @Index(name = "idx_tot_otp",      columnList = "otp")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferOtpToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The HR/Admin user who triggered the transfer */
    @Column(nullable = false)
    private String username;

    @Column(nullable = false, length = 6)
    private String otp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TransferOtpPurpose purpose;

    @Column(nullable = false)
    private LocalDateTime expiryAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean used = false;
}