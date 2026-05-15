package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "trainee_mentor_details")
@Getter
@Setter
public class TraineeMentorDetails extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "trainee_id")
    private Trainee trainee;

    @ManyToOne
    @JoinColumn(name = "mentor_employee_id")
    private Employee mentor;

    @ManyToOne
    @JoinColumn(name = "supervisor_employee_id")
    private Employee supervisor;
}