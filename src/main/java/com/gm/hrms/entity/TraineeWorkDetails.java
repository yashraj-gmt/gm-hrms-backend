package com.gm.hrms.entity;

import com.gm.hrms.enums.WorkMode;
import com.gm.hrms.enums.WorkingType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "trainee_work_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraineeWorkDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer trainingPeriodMonths;

    private LocalDate trainingStartDate;
    private LocalDate trainingEndDate;

    @OneToOne
    @JoinColumn(name = "trainee_id")
    private Trainee trainee;
}
