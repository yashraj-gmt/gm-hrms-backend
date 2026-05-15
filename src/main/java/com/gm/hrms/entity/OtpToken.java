package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "otp_tokens",
        indexes = {
                @Index(name = "idx_otp_email",  columnList = "email"),
                @Index(name = "idx_otp_token",  columnList = "otp")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 6)
    private String otp;

    @Column(nullable = false)
    private LocalDateTime expiryAt;

    @Column(nullable = false)
    private Boolean used = false;

    @Column(nullable = false, length = 30)
    private String purpose = "FORGOT_PASSWORD";
}