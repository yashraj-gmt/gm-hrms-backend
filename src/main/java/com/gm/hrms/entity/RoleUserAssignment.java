package com.gm.hrms.entity;

import com.gm.hrms.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;

/**
 * Explicit mapping: which PersonalInformation records are assigned to a given role.
 * Separate from UserAuth.role (the system role) — this is for the
 * HR-facing "who can act under HR role" assignment panel.
 */
@Entity
@Table(
        name = "role_user_assignments",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_role_person",
                columnNames = {"role_type", "personal_information_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleUserAssignment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false, length = 20)
    private RoleType roleType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_information_id", nullable = false)
    private PersonalInformation personalInformation;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "assigned_by")
    private String assignedBy;
}