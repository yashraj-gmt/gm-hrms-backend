package com.gm.hrms.entity;

import com.gm.hrms.enums.TimesheetStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "timesheets",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"person_id","work_date"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Timesheet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate workDate;

    private Integer totalMinutes;

    @Enumerated(EnumType.STRING)
    private TimesheetStatus status;

    private LocalDateTime submittedAt;

    private Long approvedBy;

    private LocalDateTime approvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private PersonalInformation person;

    @OneToMany(
            mappedBy = "timesheet",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<TimesheetEntry> entries;
}