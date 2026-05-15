package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "internship_domains",
        uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Getter
@Setter
public class InternshipDomain extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;  // Flutter App Development

    private String description;

    @Column(nullable = false)
    private Boolean active = true;
}