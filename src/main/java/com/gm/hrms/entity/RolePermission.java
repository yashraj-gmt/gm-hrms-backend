package com.gm.hrms.entity;

import com.gm.hrms.enums.ModuleType;
import com.gm.hrms.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "role_permissions",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_role_module",
                columnNames = {"role_type", "module"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePermission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false, length = 20)
    private RoleType roleType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ModuleType module;

    @Column(name = "can_view",   nullable = false)
    private Boolean canView   = false;

    @Column(name = "can_create", nullable = false)
    private Boolean canCreate = false;

    @Column(name = "can_edit",   nullable = false)
    private Boolean canEdit   = false;

    @Column(name = "can_delete", nullable = false)
    private Boolean canDelete = false;

    /** Convenience: true when all 4 are true. Computed & stored for fast reads. */
    @Column(name = "can_all",    nullable = false)
    private Boolean canAll = false;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}