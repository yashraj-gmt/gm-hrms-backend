package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "trainees")
@Getter
@Setter
public class Trainee extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, name = "trainee_code")
    private String traineeCode;

    @OneToOne
    @JoinColumn(name = "personal_information_id")
    private PersonalInformation personalInformation;

    @OneToOne(mappedBy = "trainee", cascade = CascadeType.ALL, orphanRemoval = true)
    private TraineeTrainingDetails trainingDetails;

    @OneToOne(mappedBy = "trainee", cascade = CascadeType.ALL, orphanRemoval = true)
    private TraineeEducationDetails educationDetails;

    @OneToOne(mappedBy = "trainee", cascade = CascadeType.ALL, orphanRemoval = true)
    private TraineeMentorDetails mentorDetails;
}