package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "project_assignments")
@Data
public class ProjectAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_in_project")
    private String roleInProject;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
}
