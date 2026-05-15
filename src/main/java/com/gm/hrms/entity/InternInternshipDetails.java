package com.gm.hrms.entity;

import com.gm.hrms.enums.InternShipType;
import com.gm.hrms.enums.WorkMode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "intern_internship_details")
@Getter
@Setter
public class InternInternshipDetails extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "intern_id")
    private Intern intern;

    @ManyToOne
    @JoinColumn(name = "domain_id")
    private InternshipDomain domain;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "training_period_months")
    private Integer trainingPeriodMonths;

    private Double stipend;

    @Enumerated(EnumType.STRING)
    @Column(name = "internship_type")
    private InternShipType internshipType;
}