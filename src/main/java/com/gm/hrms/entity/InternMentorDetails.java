package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "intern_mentor_details")
@Getter
@Setter
public class InternMentorDetails extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "intern_id")
    private Intern intern;

    @ManyToOne
    @JoinColumn(name = "mentor_employee_id")
    private Employee mentor;

    @ManyToOne
    @JoinColumn(name = "supervisor_employee_id")
    private Employee supervisor;
}