package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "sub_departments")
@SQLRestriction("is_deleted = false")   // ← auto-filters every query
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubDepartment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    private String description;

    @Column(nullable = false)
    private Boolean status;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean deleted = Boolean.FALSE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
}