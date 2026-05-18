package com.gm.hrms.entity;

import com.gm.hrms.enums.BreakCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "break_policies")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BreakPolicy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "break_name")
    private String breakName;

    @Enumerated(EnumType.STRING)
    @Column(name = "break_category")
    private BreakCategory breakCategory;

    @Column(name = "break_start")
    private LocalTime breakStart;

    @Column(name = "break_end")
    private LocalTime breakEnd;

    @Column(name = "break_duration_minutes")
    private Integer breakDurationMinutes;

    @Column(name = "is_paid")
    private Boolean isPaid;

    /**
     * Controls the Active/Inactive status badge in the listing.
     * false = record is visible in listing but shown as "Inactive".
     * Set via the Edit form's status toggle (ADMIN + HR).
     */
    @Column(name = "is_active")
    private Boolean isActive;

    /**
     * Controls soft-delete visibility.
     * true  = record is hidden from all listings (soft-deleted).
     * false = record appears in the listing (active or inactive).
     * Set only via the Delete action (ADMIN only).
     */
    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
}