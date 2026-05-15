package com.gm.hrms.entity;

import com.gm.hrms.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_auth",
        indexes = {
                @Index(name = "idx_auth_username", columnList = "username"),
                @Index(name = "idx_auth_personal_information", columnList = "personal_information_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAuth extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 LINK WITH PERSONAL INFORMATION (IDENTITY ROOT)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "personal_information_id",
            nullable = false,
            unique = true
    )
    private PersonalInformation personalInformation;

    // LOGIN EMAIL (official OR personal)
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Boolean accountLocked = false;

    @Column(nullable = false)
    private Boolean isLoggedIn = false;

    @Column(nullable = false)
    private Integer failedLoginAttempts = 0;

    private LocalDateTime lastLoginAt;
}