package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_types",
        uniqueConstraints = @UniqueConstraint(columnNames = "code"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false, unique = true, length = 10)
    private String code;

    private String description;

    private Boolean isPaid;

    private Boolean allowHalfDay;

    private Boolean isActive;
    
    @Column(nullable = false)
    private Boolean isCompOff;

    @Column(nullable = false)
    private Boolean allowDuringProbation;

    private Boolean isSystemDefined;

    private LocalDateTime deletedAt;
}