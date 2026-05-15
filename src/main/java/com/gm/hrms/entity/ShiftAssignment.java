package com.gm.hrms.entity;

import com.gm.hrms.enums.AssignmentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(
        name = "shift_assignments",
        indexes = {
                @Index(name = "idx_sa_pi",     columnList = "personal_information_id"),
                @Index(name = "idx_sa_shift",  columnList = "shift_id"),
                @Index(name = "idx_sa_status", columnList = "status, is_active")
        }
)
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ShiftAssignment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_information_id", nullable = false)
    private PersonalInformation personalInformation;

    @Column(nullable = false)
    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssignmentStatus status;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(nullable = false)
    private Boolean isActive;
}