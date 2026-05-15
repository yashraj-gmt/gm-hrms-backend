package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "trainee_education_details")
@Getter
@Setter
public class TraineeEducationDetails extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "trainee_id")
    private Trainee trainee;

    @Column(name = "hsc_completion")
    private String hscCompletion;

    @Column(name = "hsc_year")
    private Integer hscYear;

    @Column(name = "bachelor_completion")
    private String bachelorCompletion;

    @Column(name = "bachelor_year")
    private Integer bachelorYear;

    @Column(name = "master_completion")
    private String masterCompletion;

    @Column(name = "master_year")
    private Integer masterYear;

    @Column(name = "degree_name")
    private String degreeName;

    @Column(name = "degree_result")
    private String degreeResult;

    @Column(name = "university_name")
    private String universityName;

    @Column(name = "university_address")
    private String universityAddress;

    @Column(name = "training_completion_status")
    private String trainingCompletionStatus;
}