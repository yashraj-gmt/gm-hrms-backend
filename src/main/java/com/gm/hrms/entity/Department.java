package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments")
@SQLRestriction("is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    private String description;

    @Column(nullable = false)
    private Boolean status;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean deleted = Boolean.FALSE;             // soft-delete flag

    @OneToMany(
            mappedBy      = "department",
            cascade       = CascadeType.ALL,
            orphanRemoval = true,
            fetch         = FetchType.LAZY
    )
    @Builder.Default
    private List<SubDepartment> subDepartments = new ArrayList<>();
}