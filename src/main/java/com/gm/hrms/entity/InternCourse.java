package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "intern_courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Builder.Default
    @Column(nullable = false)
    private Boolean status = true;
}